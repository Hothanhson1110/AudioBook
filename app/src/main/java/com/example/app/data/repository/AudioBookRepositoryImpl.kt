package com.example.app.data.repository

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.app.data.local.dao.AudioBookDao
import com.example.app.data.local.dao.ChapterDao
import com.example.app.data.local.entity.AudioBookEntity
import com.example.app.data.mapper.AudioBookMapper
import com.example.app.data.parser.DocumentParserFactory
import com.example.app.data.remote.ApiService
import com.example.app.data.worker.SynthesisOrchestrator
import com.example.app.domain.model.AudioBook
import com.example.app.domain.model.BookStatus
import com.example.app.domain.model.Chapter
import com.example.app.domain.repository.AudioBookRepository
import com.example.app.domain.repository.SynthesisState
import com.example.app.domain.service.ImportProgress
import com.example.app.domain.service.VoiceProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioBookRepositoryImpl @Inject constructor(
    private val dao: AudioBookDao,
    private val chapterDao: ChapterDao,
    private val api: ApiService,
    private val parserFactory: DocumentParserFactory,
    private val mapper: AudioBookMapper,
    private val orchestrator: SynthesisOrchestrator,
    @ApplicationContext private val context: Context,
) : AudioBookRepository {

    private val workManager: WorkManager by lazy { WorkManager.getInstance(context) }

    // =========================================================================
    // Books
    // =========================================================================

    override fun observeBooks(): Flow<List<AudioBook>> =
        dao.observeAll().map { list -> list.map(mapper::toDomain) }

    override suspend fun getBook(id: Long): AudioBook? =
        dao.getById(id)?.let(mapper::toDomain)

    // =========================================================================
    // Import
    // =========================================================================

    override fun importDocument(uriString: String, title: String): Flow<ImportProgress> = flow {
        try {
            val ext = uriString.substringAfterLast('.', "").lowercase()
            require(parserFactory.isSupported(ext)) { "Unsupported format: .$ext" }

            val entity = AudioBookEntity(
                title = title,
                sourceUri = uriString,
                audioPath = null,
                durationMs = 0L,
                status = BookStatus.IMPORTED.name,
                createdAt = System.currentTimeMillis(),
            )
            val id = dao.upsert(entity)
            emit(ImportProgress.Completed(id))
        } catch (t: Throwable) {
            emit(ImportProgress.Failed(t.message ?: "Import failed"))
        }
    }.flowOn(Dispatchers.IO)

    // =========================================================================
    // Synthesis
    // =========================================================================

    override suspend fun synthesize(bookId: Long, profile: VoiceProfile) {
        val entity = dao.getById(bookId) ?: error("Book not found: $bookId")
        dao.upsert(
            entity.copy(
                voiceProfileId = profile.id,
                voiceName = profile.voiceName,
                pitch = profile.pitch,
                speechRate = profile.speechRate,
            )
        )
        orchestrator.enqueue(bookId, entity.sourceUri, profile)
    }

    override fun observeSynthesisState(bookId: Long): Flow<SynthesisState> = flow {
        val chunksFile = File(context.filesDir, "audio/book_$bookId.chunks.txt")
        val chunkDir = File(context.filesDir, "audio/book_$bookId")

        while (true) {
            val infos: List<WorkInfo> = try {
                workManager.getWorkInfosByTag("synth_book_$bookId").get()
            } catch (_: Throwable) {
                emptyList()
            }

            val totalChunks: Int = try {
                if (chunksFile.exists()) chunksFile.readLines().size else 0
            } catch (_: Throwable) {
                0
            }

            val doneChunks: Int = chunkDir
                .listFiles { f -> f.name.startsWith("chunk_") && f.extension == "wav" }
                ?.size ?: 0

            val state: SynthesisState = when {
                infos.isEmpty() -> SynthesisState.Idle

                infos.any { it.state == WorkInfo.State.FAILED } ->
                    SynthesisState.Failed("Synthesis failed")

                infos.all { it.state == WorkInfo.State.SUCCEEDED } ->
                    SynthesisState.Completed

                else -> {
                    val percent = if (totalChunks > 0) {
                        (doneChunks * 100) / totalChunks
                    } else 0
                    SynthesisState.Running(
                        progressPercent = percent.coerceIn(0, 99),
                        totalChunks = totalChunks,
                    )
                }
            }

            emit(state)

            if (state is SynthesisState.Completed || state is SynthesisState.Failed) {
                break
            }
            delay(1_000L)
        }
    }
        .catch { emit(SynthesisState.Idle) }
        .flowOn(Dispatchers.IO)

    override suspend fun cancelSynthesis(bookId: Long) = withContext(Dispatchers.IO) {
        orchestrator.cancel(bookId)

        // Reset status ve IMPORTED de user co the retry
        val book = dao.getById(bookId)
        if (book?.status == BookStatus.PROCESSING.name) {
            dao.updateAudio(bookId, null, BookStatus.IMPORTED.name, 0L)
        }

        // Xoa chunks dang do
        runCatching {
            File(context.filesDir, "audio/book_$bookId").deleteRecursively()
            File(context.filesDir, "audio/book_$bookId.chunks.txt").delete()
        }
    }

    override suspend fun isSynthesisRunning(bookId: Long): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val infos = workManager.getWorkInfosByTag("synth_book_$bookId").get()
                infos.any {
                    it.state == WorkInfo.State.RUNNING ||
                        it.state == WorkInfo.State.ENQUEUED ||
                        it.state == WorkInfo.State.BLOCKED
                }
            } catch (_: Throwable) {
                false
            }
        }

    override suspend fun markAsFailed(bookId: Long) = withContext(Dispatchers.IO) {
        val book = dao.getById(bookId)
        if (book?.status == BookStatus.PROCESSING.name) {
            dao.updateAudio(bookId, null, BookStatus.FAILED.name, 0L)
        }
    }

    override suspend fun getAvailableChunks(bookId: Long): List<File> =
        withContext(Dispatchers.IO) {
            File(context.filesDir, "audio/book_$bookId")
                .listFiles { f -> f.name.startsWith("chunk_") && f.extension == "wav" }
                ?.sortedBy { it.name }
                ?: emptyList()
        }

    // =========================================================================
    // Delete
    // =========================================================================

    override suspend fun deleteBook(id: Long) = withContext(Dispatchers.IO) {
        val entity = dao.getById(id) ?: return@withContext

        // Cancel workers dang chay
        runCatching { orchestrator.cancel(id) }

        // Xoa file output
        entity.audioPath?.let { path ->
            runCatching { File(path).takeIf(File::exists)?.delete() }
        }

        // Xoa chunk dir + files
        runCatching {
            File(context.filesDir, "audio/book_$id").deleteRecursively()
        }
        runCatching {
            File(context.filesDir, "audio/book_$id.m4a").delete()
        }
        runCatching {
            File(context.filesDir, "audio/book_$id.chunks.txt").delete()
        }

        // Xoa khoi DB
        chapterDao.deleteByBook(id)
        dao.delete(entity)
    }

    // =========================================================================
    // Playback state
    // =========================================================================

    override suspend fun updatePlayback(bookId: Long, positionMs: Long) =
        withContext(Dispatchers.IO) {
            dao.updatePlayback(bookId, positionMs, System.currentTimeMillis())
        }

    // =========================================================================
    // Chapters
    // =========================================================================

    override suspend fun getChapters(bookId: Long): List<Chapter> =
        withContext(Dispatchers.IO) {
            chapterDao.getByBook(bookId).map(mapper::toDomain)
        }

    // =========================================================================
    // Remote sync
    // =========================================================================

    override suspend fun syncFromRemote(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val remote = api.fetchBooks()
            remote.forEach { dto ->
                val existing = dao.getById(dto.id)
                val merged = if (existing != null) {
                    existing.copy(
                        title = dto.title,
                        audioPath = existing.audioPath ?: dto.audioUrl,
                    )
                } else {
                    mapper.toEntity(dto)
                }
                dao.upsert(merged)
            }
        }
    }
}
