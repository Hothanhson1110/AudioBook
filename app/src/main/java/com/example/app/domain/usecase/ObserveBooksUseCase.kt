package com.example.app.domain.usecase

import com.example.app.domain.model.AudioBook
import com.example.app.domain.repository.AudioBookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBooksUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    operator fun invoke(): Flow<List<AudioBook>> = repository.observeBooks()
}