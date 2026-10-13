package com.example.app.domain.usecase

import com.example.app.domain.model.VoiceSettings
import com.example.app.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveVoiceSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(settings: VoiceSettings) =
        repository.saveVoiceSettings(settings)
}
