package com.example.app.domain.usecase

import com.example.app.domain.repository.AudioBookRepository
import com.example.app.domain.service.SynthesisProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SynthesizeBookUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    operator fun invoke(bookId: Long): Flow<SynthesisProgress> =
        repository.synthesize(bookId)
}