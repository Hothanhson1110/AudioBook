package com.example.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_books")
data class AudioBookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val sourceUri: String,
    val audioPath: String?,
    val durationMs: Long,
    val status: String,
    val createdAt: Long,
)