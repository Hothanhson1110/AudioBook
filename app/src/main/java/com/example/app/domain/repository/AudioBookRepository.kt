package com.example.app.domain.repository

import com.example.app.domain.model.AudioBook
import com.example.app.domain.service.SynthesisProgress
import kotlinx.coroutines.flow.Flow

interface AudioBookRepository {
    fun observeBooks(): Flow<List<AudioBook>>
    suspend fun getBook(id: Long): AudioBook?
    fun importDocument(uriString: String, title: String): Flow<ImportProgress>
    fun synthesize(bookId: Long): Flow<SynthesisProgress>
    suspend fun deleteBook(id: Long)
    suspend fun syncFromRemote(): Result<Unit>
}

sealed interface ImportProgress {
    data class Completed(val bookId: Long) : ImportProgress
    data class Failed(val message: String) : ImportProgress
}