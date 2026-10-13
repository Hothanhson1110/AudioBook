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
    data class NavigateToPlayer(val bookId: Long) : HomeEvent
}

data class HomeStats(
    val total: Int = 0,
    val ready: Int = 0,
    val processing: Int = 0,
    val failed: Int = 0,
)
