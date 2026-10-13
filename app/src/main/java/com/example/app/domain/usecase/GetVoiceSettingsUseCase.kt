package com.example.app.domain.usecase

import com.example.app.domain.model.VoiceSettings
import com.example.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVoiceSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<VoiceSettings> = repository.observeVoiceSettings()
}
