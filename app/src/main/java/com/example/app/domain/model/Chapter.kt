package com.example.app.domain.model

data class Chapter(
    val id: Long = 0L,
    val bookId: Long,
    val title: String,
    val startMs: Long,
    val endMs: Long,
    val level: Int = 1,
)