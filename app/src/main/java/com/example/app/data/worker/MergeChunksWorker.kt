package com.example.app.data.worker

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.app.core.WorkerLogger
import com.example.app.data.local.dao.AudioBookDao
import com.example.app.domain.model.BookStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.coroutineContext

@HiltWorker
class MergeChunksWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val dao: AudioBookDao,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val bookId = inputData.getLong(KEY_BOOK_ID, -1L)
        WorkerLogger.log(
            applicationContext, "Merge",
            "=== START book $bookId (runId=$id, attempt=$runAttemptCount) ==="
        )

        if (bookId <= 0L) return@withContext Result.failure()

        val outDir = File(applicationContext.filesDir, "audio").apply { mkdirs() }
        val m4a = File(outDir, "book_$bookId.m4a")
        val chunkDir = File(applicationContext.filesDir, "audio/book_$bookId")
        val chunksFile = File(applicationContext.filesDir, "audio/book_$bookId.chunks.txt")

        val dbBook = dao.getById(bookId)
        if (m4a.exists() && m4a.length() > 0 && dbBook?.status == BookStatus.READY.name) {
            WorkerLogger.log(applicationContext, "Merge", "skip: đã READY")
            return@withContext Result.success(workDataOf(KEY_OUTPUT to m4a.absolutePath))
        }

        val chunks = chunkDir
            .listFiles { f -> f.name.startsWith("chunk_") && f.extension == "wav" }
            ?.sortedBy { it.name }
            .orEmpty()

        WorkerLogger.log(applicationContext, "Merge", "found ${chunks.size} chunks")

        if (chunks.isEmpty()) {
            dao.updateAudio(bookId, null, BookStatus.FAILED.name, 0L)
            return@withContext Result.failure(workDataOf(KEY_ERROR to "No chunks"))
        }

        val missing = chunks.filterNot { it.exists() && it.length() > 0 }
        if (missing.isNotEmpty()) {
            WorkerLogger.log(applicationContext, "Merge", "FAIL: ${missing.size} missing")
            return@withContext Result.failure(workDataOf(KEY_ERROR to "Missing chunks"))
        }

        return@withContext try {
            WorkerLogger.log(applicationContext, "Merge", "encoding ${chunks.size} chunks...")
            if (m4a.exists()) m4a.delete()

            WavToAacEncoder.encode(chunks, m4a)
            WorkerLogger.log(applicationContext, "Merge", "encoded OK size=${m4a.length()}")

            coroutineContext.ensureActive()

            val durationMs = readDurationMs(m4a)
            dao.updateAudio(bookId, m4a.absolutePath, BookStatus.READY.name, durationMs)
            WorkerLogger.log(applicationContext, "Merge", "DONE duration=$durationMs ms")

            chunkDir.deleteRecursively()
            chunksFile.delete()
            WorkerLogger.log(applicationContext, "Merge", "cleanup OK")

            Result.success(workDataOf(KEY_OUTPUT to m4a.absolutePath))
        } catch (t: CancellationException) {
            WorkerLogger.log(applicationContext, "Merge", "CANCELLED")
            throw t
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
            runCatching { retriever.release() }
        }
    }

    companion object {
        const val KEY_BOOK_ID = "book_id"
        const val KEY_CHUNKS = "chunks"
        const val KEY_OUTPUT = "output"
        const val KEY_ERROR = "error"
    }
}
