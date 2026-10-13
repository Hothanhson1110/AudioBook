package com.example.app.data.worker

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.app.data.local.dao.AudioBookDao
import com.example.app.domain.model.BookStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import com.example.app.core.WorkerLogger

@HiltWorker
class MergeChunksWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val dao: AudioBookDao,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val bookId = inputData.getLong(KEY_BOOK_ID, -1L)
        WorkerLogger.log(applicationContext, "Merge", "=== START book $bookId ===")

        if (bookId <= 0L) return@withContext Result.failure()

        val chunkDir = File(applicationContext.filesDir, "audio/book_$bookId")
        val actualChunks = chunkDir
            .listFiles { f -> f.name.startsWith("chunk_") && f.extension == "wav" }
            ?.sortedBy { it.name }
            .orEmpty()

        WorkerLogger.log(applicationContext, "Merge", "found ${actualChunks.size} chunks")

        if (actualChunks.isEmpty()) {
            WorkerLogger.log(applicationContext, "Merge", "FAIL: no chunks")
            dao.updateAudio(bookId, null, BookStatus.FAILED.name, 0L)
            return@withContext Result.failure(workDataOf(KEY_ERROR to "No chunks"))
        }

        return@withContext try {
            val outDir = File(applicationContext.filesDir, "audio").apply { mkdirs() }
            val m4a = File(outDir, "book_$bookId.m4a")

            WorkerLogger.log(applicationContext, "Merge", "encoding to ${m4a.absolutePath}")
            WavToAacEncoder.encode(actualChunks, m4a)
            WorkerLogger.log(applicationContext, "Merge", "encoded OK, size=${m4a.length()}")

            chunkDir.deleteRecursively()

            val durationMs = readDurationMs(m4a)
            dao.updateAudio(bookId, m4a.absolutePath, BookStatus.READY.name, durationMs)
            WorkerLogger.log(applicationContext, "Merge", "DONE duration=$durationMs")
            Result.success(workDataOf(KEY_OUTPUT to m4a.absolutePath))
        } catch (t: Throwable) {
            WorkerLogger.logError(applicationContext, "Merge", t)
            dao.updateAudio(bookId, null, BookStatus.FAILED.name, 0L)
            Result.failure(workDataOf(KEY_ERROR to (t.message ?: "unknown")))
        }
    }

    private fun readDurationMs(file: File): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
        } catch (_: Throwable) {
            0L
        } finally {
            retriever.release()
        }
    }

    companion object {
        const val KEY_BOOK_ID = "book_id"
        const val KEY_CHUNKS = "chunks"
        const val KEY_OUTPUT = "output"
        const val KEY_ERROR = "error"
    }
}