package com.example.app.domain.repository

import com.example.app.domain.model.AudioBook
import com.example.app.domain.model.Chapter
import com.example.app.domain.service.ImportProgress
import com.example.app.domain.service.VoiceProfile
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AudioBookRepository {
    fun observeBooks(): Flow<List<AudioBook>>
    suspend fun getBook(id: Long): AudioBook?
    fun importDocument(uriString: String, title: String): Flow<ImportProgress>
    suspend fun synthesize(bookId: Long, profile: VoiceProfile)
    fun observeSynthesisState(bookId: Long): Flow<SynthesisState>
    suspend fun cancelSynthesis(bookId: Long)
    suspend fun deleteBook(id: Long)
    suspend fun updatePlayback(bookId: Long, positionMs: Long)
    suspend fun getChapters(bookId: Long): List<Chapter>
    suspend fun getAvailableChunks(bookId: Long): List<File>
    suspend fun syncFromRemote(): Result<Unit>
}

sealed interface SynthesisState {
    data object Idle : SynthesisState
    data class Running(
        val progressPercent: Int,
        val totalChunks: Int = 0,
    ) : SynthesisState
    data object Completed : SynthesisState
    data class Failed(val message: String) : SynthesisState
}
