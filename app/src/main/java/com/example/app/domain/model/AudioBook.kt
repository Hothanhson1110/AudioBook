package com.example.app.domain.model

data class AudioBook(
    val id: Long = 0L,
    val title: String,
    val sourceUri: String,
    val audioPath: String?,
    val durationMs: Long,
    val status: BookStatus,
    val createdAt: Long,
)

enum class BookStatus { IMPORTED, PROCESSING, READY, FAILED }