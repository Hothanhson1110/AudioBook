package com.example.app.domain.usecase

import com.example.app.domain.model.AudioBook
import com.example.app.domain.repository.AudioBookRepository
import javax.inject.Inject

class GetBookUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    suspend operator fun invoke(id: Long): AudioBook? = repository.getBook(id)
}