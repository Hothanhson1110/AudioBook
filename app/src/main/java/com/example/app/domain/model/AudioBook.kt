package com.example.app.domain.model

data class AudioBook(
    val id: Long = 0L,
    val title: String,
    val sourceUri: String,
    val audioPath: String?,
    val durationMs: Long,
    val status: BookStatus,
    val createdAt: Long,
    val voiceProfileId: String = "vi_natural",
    val voiceName: String = "",
    val pitch: Float = 1f,
    val speechRate: Float = 1f,
    val lastPositionMs: Long = 0L,
    val lastPlayedAt: Long = 0L,
)

enum class BookStatus { IMPORTED, PROCESSING, READY, FAILED }