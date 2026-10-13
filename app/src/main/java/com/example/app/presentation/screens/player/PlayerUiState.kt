package com.example.app.presentation.screens.player

import com.example.app.domain.model.Chapter

data class PlayerUiState(
    val title: String = "",
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val speed: Float = 1f,
    val chapters: List<Chapter> = emptyList(),
    val currentChapter: Int = 1,
    val sleepTimerRemainingMs: Long = 0L,
    val isLoading: Boolean = true,
    val error: String? = null,
)