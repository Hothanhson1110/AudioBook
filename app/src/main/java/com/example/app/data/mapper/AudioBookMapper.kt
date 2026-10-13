package com.example.app.data.mapper

import com.example.app.data.local.entity.AudioBookEntity
import com.example.app.data.local.entity.ChapterEntity
import com.example.app.data.remote.dto.AudioBookDto
import com.example.app.domain.model.AudioBook
import com.example.app.domain.model.BookStatus
import com.example.app.domain.model.Chapter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioBookMapper @Inject constructor() {

    fun toDomain(entity: AudioBookEntity): AudioBook = AudioBook(
        id = entity.id,
        title = entity.title,
        sourceUri = entity.sourceUri,
        audioPath = entity.audioPath,
        durationMs = entity.durationMs,
        status = runCatching { BookStatus.valueOf(entity.status) }
            .getOrDefault(BookStatus.IMPORTED),
        createdAt = entity.createdAt,
        voiceProfileId = entity.voiceProfileId,
        voiceName = entity.voiceName,
        pitch = entity.pitch,
        speechRate = entity.speechRate,
        lastPositionMs = entity.lastPositionMs,
        lastPlayedAt = entity.lastPlayedAt,
    )

    fun toEntity(domain: AudioBook): AudioBookEntity = AudioBookEntity(
        id = domain.id,
        title = domain.title,
        sourceUri = domain.sourceUri,
        audioPath = domain.audioPath,
        durationMs = domain.durationMs,
        status = domain.status.name,
        createdAt = domain.createdAt,
        voiceProfileId = domain.voiceProfileId,
        voiceName = domain.voiceName,
        pitch = domain.pitch,
        speechRate = domain.speechRate,
        lastPositionMs = domain.lastPositionMs,
        lastPlayedAt = domain.lastPlayedAt,
    )

    fun toEntity(dto: AudioBookDto): AudioBookEntity = AudioBookEntity(
        id = dto.id,
        title = dto.title,
        sourceUri = dto.sourceUrl,
        audioPath = dto.audioUrl,
        durationMs = 0L,
        status = if (dto.audioUrl != null) BookStatus.READY.name else BookStatus.IMPORTED.name,
        createdAt = System.currentTimeMillis(),
    )

    fun toDomain(entity: ChapterEntity): Chapter = Chapter(
        id = entity.id,
        bookId = entity.bookId,
        title = entity.title,
        startMs = entity.startMs,
        endMs = entity.endMs,
        level = entity.level,
    )
}