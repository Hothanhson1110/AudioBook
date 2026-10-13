package com.example.app.presentation.screens.library

import com.example.app.domain.model.AudioBook

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data class Success(val readyBooks: List<AudioBook>) : LibraryUiState
}