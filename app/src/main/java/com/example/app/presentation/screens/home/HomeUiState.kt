package com.example.app.presentation.screens.home

import com.example.app.domain.model.AudioBook

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val books: List<AudioBook>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

sealed interface HomeEvent {
    data class Message(val text: String) : HomeEvent
    data class Imported(val bookId: Long) : HomeEvent
}

data class SynthesisUiState(
    val bookId: Long,
    val percent: Int,
)