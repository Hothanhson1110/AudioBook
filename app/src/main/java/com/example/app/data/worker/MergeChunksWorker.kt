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

@HiltWorker
class MergeChunksWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val dao: AudioBookDao,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val bookId = inputData.getLong(KEY_BOOK_ID, -1L)
        val chunkPaths = inputData.getStringArray(KEY_CHUNKS)?.toList()
            ?: return@withContext Result.failure()

        if (bookId <= 0L || chunkPaths.isEmpty()) {
            return@withContext Result.failure()
        }

        val chunkDir = File(applicationContext.filesDir, "audio/book_$bookId")
        val actualChunks = if (chunkPaths.isNotEmpty()) {
            chunkPaths.map(::File)
        } else {
            chunkDir.listFiles { f -> f.name.startsWith("chunk_") }
                ?.sortedBy { it.name }
                ?: emptyList()
        }

        try {
            require(actualChunks.isNotEmpty() && actualChunks.all { it.exists() }) {
                "Missing chunk files"
            }

            val outDir = File(applicationContext.filesDir, "audio").apply { mkdirs() }
            val m4a = File(outDir, "book_$bookId.m4a")

            WavToAacEncoder.encode(actualChunks.sortedBy { it.name }, m4a)
            chunkDir.deleteRecursively()

            val durationMs = readDurationMs(m4a)
            dao.updateAudio(
                id = bookId,
                path = m4a.absolutePath,
                status = BookStatus.READY.name,
                duration = durationMs,
            )
            Result.success(workDataOf(KEY_OUTPUT to m4a.absolutePath))
        } catch (t: Throwable) {
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