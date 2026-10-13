package com.example.app.domain.usecase

import com.example.app.domain.repository.AudioBookRepository
import javax.inject.Inject

class UpdatePlaybackUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    suspend operator fun invoke(bookId: Long, positionMs: Long) =
        repository.updatePlayback(bookId, positionMs)
}