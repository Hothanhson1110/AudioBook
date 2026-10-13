package com.example.app.domain.repository

import com.example.app.domain.model.VoiceSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeVoiceSettings(): Flow<VoiceSettings>
    suspend fun saveVoiceSettings(settings: VoiceSettings)
}
