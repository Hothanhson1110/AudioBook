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

    // Lazy — chỉ gọi WorkManager.getInstance khi thực sự cần
    private val workManager: WorkManager by lazy { WorkManager.getInstance(context) }

    override fun observeBooks(): Flow<List<AudioBook>> =
        dao.observeAll().map { list -> list.map(mapper::toDomain) }

    override suspend fun getBook(id: Long): AudioBook? =
        dao.getById(id)?.let(mapper::toDomain)

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

    override fun observeSynthesisState(bookId: Long): Flow<SynthesisState> =
        workManager
            .getWorkInfosByTagFlow("synth_book_$bookId")
            .map { infos ->
                if (infos.isEmpty()) return@map SynthesisState.Idle

                val anyFailed = infos.any { it.state == WorkInfo.State.FAILED }
                if (anyFailed) return@map SynthesisState.Failed("Synthesis failed")

                val allSucceeded = infos.all { it.state == WorkInfo.State.SUCCEEDED }
                if (allSucceeded) return@map SynthesisState.Completed

                val running = infos.count { it.state == WorkInfo.State.RUNNING }
                val total = infos.size
                val percent = if (total > 0) (running * 100) / total else 0
                SynthesisState.Running(percent.coerceIn(0, 99))
            }
            .catch { emit(SynthesisState.Idle) }
            .flowOn(Dispatchers.Default)

    override suspend fun cancelSynthesis(bookId: Long) =
        withContext(Dispatchers.IO) { orchestrator.cancel(bookId) }

    override suspend fun deleteBook(id: Long) = withContext(Dispatchers.IO) {
        val entity = dao.getById(id) ?: return@withContext
        entity.audioPath?.let { path ->
            runCatching { File(path).takeIf(File::exists)?.delete() }
        }
        val chunkDir = File(context.filesDir, "audio/book_$id")
        if (chunkDir.exists()) chunkDir.deleteRecursively()
        chapterDao.deleteByBook(id)
        dao.delete(entity)
    }

    override suspend fun updatePlayback(bookId: Long, positionMs: Long) =
        withContext(Dispatchers.IO) {
            dao.updatePlayback(bookId, positionMs, System.currentTimeMillis())
        }

    override suspend fun getChapters(bookId: Long): List<Chapter> =
        withContext(Dispatchers.IO) {
            chapterDao.getByBook(bookId).map(mapper::toDomain)
        }

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