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
    val totalChapters: Int = 1,
    val sleepTimerRemainingMs: Long = 0L,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isBookmarked: Boolean = false,
    val isShuffleOn: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isBuffering: Boolean = false,
    val isStreamingMode: Boolean = false,   // true khi đang phát chunks
    val streamingProgress: Int = 0,          // 0-100 cho badge
)

enum class RepeatMode { OFF, ONE, ALL }
