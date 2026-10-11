package com.example.app.data.repository

import android.content.Context
import android.media.MediaMetadataRetriever
import com.example.app.data.local.dao.AudioBookDao
import com.example.app.data.local.entity.AudioBookEntity
import com.example.app.data.mapper.AudioBookMapper
import com.example.app.data.parser.DocumentParserFactory
import com.example.app.data.remote.ApiService
import com.example.app.domain.model.AudioBook
import com.example.app.domain.model.BookStatus
import com.example.app.domain.repository.AudioBookRepository
import com.example.app.domain.repository.ImportProgress
import com.example.app.domain.service.SynthesisProgress
import com.example.app.domain.service.TextToSpeechService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
    private val api: ApiService,
    private val parserFactory: DocumentParserFactory,
    private val tts: TextToSpeechService,
    private val mapper: AudioBookMapper,
    @ApplicationContext private val context: Context,
) : AudioBookRepository {

    override fun observeBooks(): Flow<List<AudioBook>> =
        dao.observeAll().map { list -> list.map(mapper::toDomain) }

    override suspend fun getBook(id: Long): AudioBook? =
        dao.getById(id)?.let(mapper::toDomain)

    override fun importDocument(uriString: String, title: String): Flow<ImportProgress> = flow {
        try {
            val ext = uriString.substringAfterLast('.', "").lowercase()
            require(parserFactory.isSupported(ext)) { "Định dạng .$ext chưa được hỗ trợ" }

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
            emit(ImportProgress.Failed(t.message ?: "Import thất bại"))
        }
    }.flowOn(Dispatchers.IO)

    override fun synthesize(bookId: Long): Flow<SynthesisProgress> = flow {
        try {
            val entity = dao.getById(bookId) ?: error("Không tìm thấy sách: $bookId")
            dao.updateAudio(bookId, entity.audioPath, BookStatus.PROCESSING.name, 0L)

            val ext = entity.sourceUri.substringAfterLast('.', "").lowercase()
            val parser = parserFactory.parserFor(ext)
                ?: error("Định dạng .$ext chưa được hỗ trợ")

            // Gom toàn bộ text (chunk-by-chunk) thành 1 string
            val fullText = buildString {
                parser.parse(entity.sourceUri).collect { chunk -> appendLine(chunk) }
            }

            val audioDir = File(context.filesDir, "audio").apply { mkdirs() }
            val outputFile = File(audioDir, "book_$bookId.wav")

            tts.synthesize(fullText, outputFile).collect { progress ->
                emit(progress)
                when (progress) {
                    is SynthesisProgress.Completed -> {
                        val duration = readDurationMs(outputFile)
                        dao.updateAudio(
                            id = bookId,
                            path = outputFile.absolutePath,
                            status = BookStatus.READY.name,
                            duration = duration,
                        )
                    }
                    is SynthesisProgress.Failed -> {
                        dao.updateAudio(bookId, null, BookStatus.FAILED.name, 0L)
                    }
                    is SynthesisProgress.Progress -> Unit
                }
            }
        } catch (t: Throwable) {
            emit(SynthesisProgress.Failed(t.message ?: "Synthesize thất bại"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteBook(id: Long) = withContext(Dispatchers.IO) {
        val entity = dao.getById(id) ?: return@withContext
        entity.audioPath?.let { path ->
            runCatching { File(path).takeIf(File::exists)?.delete() }
        }
        dao.delete(entity)
    }

    override suspend fun syncFromRemote(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val remote = api.fetchBooks()
            remote.forEach { dao.upsert(mapper.toEntity(it)) }
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
}