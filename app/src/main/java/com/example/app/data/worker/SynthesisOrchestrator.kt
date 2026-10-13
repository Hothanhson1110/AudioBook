package com.example.app.data.worker

import android.content.Context
import androidx.work.Constraints
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
        WorkerLogger.log(context, "Orchestrator", "=== START book $bookId ===")
        WorkerLogger.log(context, "Orchestrator", "uri=$sourceUri profile=${profile.id}")

        dao.updateAudio(bookId, null, BookStatus.PROCESSING.name, 0L)

        try {
            val ext = sourceUri.substringAfterLast('.', "").lowercase()
            val parser = parserFactory.parserFor(ext) ?: error("Unsupported: .$ext")

            WorkerLogger.log(context, "Orchestrator", "parsing...")
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
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiresBatteryNotLow(false)
                            .build()
                    )
                    .addTag(tag(bookId))
                    .build()
            }

            val mergeRequest = OneTimeWorkRequestBuilder<MergeChunksWorker>()
                .setInputData(workDataOf(MergeChunksWorker.KEY_BOOK_ID to bookId))
                .addTag(tag(bookId))
                .build()

            workManager
                .beginWith(chunkRequests)
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
        workManager.cancelAllWorkByTag(tag(bookId))
    }

    private fun tag(bookId: Long) = "synth_book_$bookId"
}