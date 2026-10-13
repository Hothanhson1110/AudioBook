package com.example.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.model.VoiceAge
import com.example.app.domain.model.VoiceGender
import com.example.app.domain.model.VoiceStyle
import com.example.app.domain.usecase.GetVoiceSettingsUseCase
import com.example.app.domain.usecase.SaveVoiceSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getVoiceSettings: GetVoiceSettingsUseCase,
    private val saveVoiceSettings: SaveVoiceSettingsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getVoiceSettings()
            .onEach { settings ->
                _uiState.value = _uiState.value.copy(settings = settings)
            }
            .launchIn(viewModelScope)
    }

    fun onGenderChange(gender: VoiceGender) {
        _uiState.value = _uiState.value.copy(
            settings = _uiState.value.settings.copy(gender = gender),
            saved = false,
        )
        save()
    }

    fun onAgeChange(age: VoiceAge) {
        _uiState.value = _uiState.value.copy(
            settings = _uiState.value.settings.copy(age = age),
            saved = false,
        )
        save()
    }

    fun onStyleChange(style: VoiceStyle) {
        _uiState.value = _uiState.value.copy(
            settings = _uiState.value.settings.copy(style = style),
            saved = false,
        )
        save()
    }

    private fun save() {
        viewModelScope.launch {
            saveVoiceSettings(_uiState.value.settings)
            _uiState.value = _uiState.value.copy(saved = true)
        }
    }
}
