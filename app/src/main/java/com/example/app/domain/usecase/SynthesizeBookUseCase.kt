package com.example.app.domain.usecase

import com.example.app.domain.repository.AudioBookRepository
import com.example.app.domain.repository.SynthesisState
import com.example.app.domain.service.VoiceProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SynthesizeBookUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    suspend fun start(bookId: Long, profile: VoiceProfile) =
        repository.synthesize(bookId, profile)

    fun observeState(bookId: Long): Flow<SynthesisState> =
        repository.observeSynthesisState(bookId)

    suspend fun cancel(bookId: Long) =
        repository.cancelSynthesis(bookId)

    suspend fun isRunning(bookId: Long): Boolean =
        repository.isSynthesisRunning(bookId)

    suspend fun markFailed(bookId: Long) =
        repository.markAsFailed(bookId)
}
