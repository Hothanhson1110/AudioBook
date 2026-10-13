package com.example.app.data.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.app.core.WorkerLogger
import com.example.app.data.local.dao.AudioBookDao
import com.example.app.data.parser.DocumentParserFactory
import com.example.app.domain.model.BookStatus
import com.example.app.domain.service.TextChunker
import com.example.app.domain.service.VoiceProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SynthesisOrchestrator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: AudioBookDao,
    private val parserFactory: DocumentParserFactory,
    private val chunker: TextChunker,
) {
    private val workManager: WorkManager = WorkManager.getInstance(context)

    suspend fun enqueue(
        bookId: Long,
        sourceUri: String,
        profile: VoiceProfile,
    ) = withContext(Dispatchers.IO) {
        // Nếu book đã đang PROCESSING → bỏ qua, không tạo work trùng
        val existing = dao.getById(bookId)
        if (existing?.status == BookStatus.PROCESSING.name) {
            WorkerLogger.log(context, "Orchestrator", "skip: book $bookId đang PROCESSING")
            return@withContext
        }

        WorkerLogger.log(context, "Orchestrator", "=== START book $bookId ===")

        // Chỉ xoá file output cũ (.m4a), KHÔNG xoá chunkDir
        File(context.filesDir, "audio/book_$bookId.m4a").delete()

        dao.updateAudio(bookId, null, BookStatus.PROCESSING.name, 0L)

        try {
            val ext = sourceUri.substringAfterLast('.', "").lowercase()
            val parser = parserFactory.parserFor(ext) ?: error("Unsupported: .$ext")

            val fullText = buildString {
                parser.parse(sourceUri).collect { appendLine(it) }
            }
            WorkerLogger.log(context, "Orchestrator", "parsed: ${fullText.length} chars")

            val chunks = chunker.chunk(fullText)
            WorkerLogger.log(context, "Orchestrator", "chunks: ${chunks.size}")
            require(chunks.isNotEmpty()) { "No text to synthesize" }

            val chunkRequests = chunks.mapIndexed { index, text ->
                OneTimeWorkRequestBuilder<TtsChunkWorker>()
                    .setInputData(
                        workDataOf(
                            TtsChunkWorker.KEY_BOOK_ID to bookId,
                            TtsChunkWorker.KEY_INDEX to index,
                            TtsChunkWorker.KEY_TEXT to text,
                            TtsChunkWorker.KEY_VOICE to profile.voiceName,
                            TtsChunkWorker.KEY_LOCALE to profile.localeTag,
                            TtsChunkWorker.KEY_PITCH to profile.pitch,
                            TtsChunkWorker.KEY_RATE to profile.speechRate,
                        )
                    )
                    .addTag(tag(bookId))
                    .build()
            }

            val mergeRequest = OneTimeWorkRequestBuilder<MergeChunksWorker>()
                .setInputData(workDataOf(MergeChunksWorker.KEY_BOOK_ID to bookId))
                .addTag(tag(bookId))
                .build()

            // KEEP: nếu đã có unique work cùng tên → bỏ qua, không chạy song song
            workManager
                .beginUniqueWork(tag(bookId), ExistingWorkPolicy.KEEP, chunkRequests)
                .then(mergeRequest)
                .enqueue()

            WorkerLogger.log(context, "Orchestrator", "enqueued ${chunkRequests.size} workers")
        } catch (t: Throwable) {
            WorkerLogger.logError(context, "Orchestrator", t)
            dao.updateAudio(bookId, null, BookStatus.FAILED.name, 0L)
            throw t
        }
    }

    fun cancel(bookId: Long) {
        WorkerLogger.log(context, "Orchestrator", "cancel book $bookId")
        workManager.cancelAllWorkByTag(tag(bookId))
        // Xoá chunks khi user chủ động cancel
        File(context.filesDir, "audio/book_$bookId").deleteRecursively()
    }

    private fun tag(bookId: Long) = "synth_book_$bookId"
}a
