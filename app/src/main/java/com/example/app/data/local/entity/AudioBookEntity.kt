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
    val voiceProfileId: String = "vi_natural",
    val voiceName: String = "",
    val pitch: Float = 1.0f,
    val speechRate: Float = 1.0f,
    val lastPositionMs: Long = 0L,
    val lastPlayedAt: Long = 0L,
)