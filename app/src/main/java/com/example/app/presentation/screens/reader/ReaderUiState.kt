// ReaderUiState.kt
package com.example.app.presentation.screens.reader

import com.example.app.domain.model.AudioBook

sealed interface ReaderUiState {
    data object Loading : ReaderUiState
    data object NotFound : ReaderUiState
    data class Ready(
        val book: AudioBook,
        val isPlaying: Boolean = false,
        val positionMs: Long = 0L,
    ) : ReaderUiState
}