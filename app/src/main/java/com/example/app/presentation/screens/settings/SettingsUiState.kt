package com.example.app.presentation.screens.settings

import com.example.app.domain.model.VoiceSettings

data class SettingsUiState(
    val settings: VoiceSettings = VoiceSettings(),
    val saved: Boolean = false,
)
