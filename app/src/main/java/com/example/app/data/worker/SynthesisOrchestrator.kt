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
        val existing = dao.getById(bookId)
        if (existing?.status == BookStatus.PROCESSING.name) {
            WorkerLogger.log(context, "Orchestrator", "skip: $bookId đang PROCESSING")
            return@withContext
        }

        WorkerLogger.log(context, "Orchestrator", "=== START book $bookId ===")

        File(context.filesDir, "audio/book_$bookId.m4a").delete()
        File(context.filesDir, "audio/book_$bookId").deleteRecursively()

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

            // Chia thành tối đa 4 batch chạy song song
            val batchCount = minOf(PARALLEL_LANES, chunks.size)
            val batchSize = (chunks.size + batchCount - 1) / batchCount

            val batchRequests = (0 until batchCount).map { batchIdx ->
                val start = batchIdx * batchSize
                val end = minOf(start + batchSize, chunks.size)
                val batchTexts = chunks.subList(start, end).toTypedArray()

                OneTimeWorkRequestBuilder<TtsBatchWorker>()
                    .setInputData(
                        workDataOf(
                            TtsBatchWorker.KEY_BOOK_ID to bookId,
                            TtsBatchWorker.KEY_BATCH_INDEX to batchIdx,
                            TtsBatchWorker.KEY_START_INDEX to start,
                            TtsBatchWorker.KEY_TEXTS to batchTexts,
                            TtsBatchWorker.KEY_VOICE to profile.voiceName,
                            TtsBatchWorker.KEY_LOCALE to profile.localeTag,
                            TtsBatchWorker.KEY_PITCH to profile.pitch,
                            TtsBatchWorker.KEY_RATE to profile.speechRate,
                        )
                    )
                    .addTag(tag(bookId))
                    .build()
            }

            val mergeRequest = OneTimeWorkRequestBuilder<MergeChunksWorker>()
                .setInputData(workDataOf(MergeChunksWorker.KEY_BOOK_ID to bookId))
                .addTag(tag(bookId))
                .build()

            workManager
                .beginUniqueWork(tag(bookId), ExistingWorkPolicy.KEEP, batchRequests)
                .then(mergeRequest)
                .enqueue()

            WorkerLogger.log(
                context, "Orchestrator",
                "enqueued $batchCount batches × $batchSize chunks"
            )
        } catch (t: Throwable) {
            WorkerLogger.logError(context, "Orchestrator", t)
            dao.updateAudio(bookId, null, BookStatus.FAILED.name, 0L)
            throw t
        }
    }

    fun cancel(bookId: Long) {
        WorkerLogger.log(context, "Orchestrator", "cancel $bookId")
        workManager.cancelAllWorkByTag(tag(bookId))
        File(context.filesDir, "audio/book_$bookId").deleteRecursively()
    }

    private fun tag(bookId: Long) = "synth_book_$bookId"

    companion object {
        // Số batch chạy song song. 4 là sweet spot:
        // - Đủ nhanh (dùng 4 core)
        // - Không overwhelm TTS engine
        // - RAM an toàn (~4 TTS instance)
        private const val PARALLEL_LANES = 4
    }
}
