package com.example.app.domain.usecase

import com.example.app.domain.repository.AudioBookRepository
import javax.inject.Inject

class DeleteBookUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deleteBook(id)
}