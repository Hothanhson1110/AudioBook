package com.example.app.data.mapper

import com.example.app.data.local.entity.AudioBookEntity
import com.example.app.data.remote.dto.AudioBookDto
import com.example.app.domain.model.AudioBook
import com.example.app.domain.model.BookStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioBookMapper @Inject constructor() {

    fun toDomain(entity: AudioBookEntity) = AudioBook(
        id = entity.id,
        title = entity.title,
        sourceUri = entity.sourceUri,
        audioPath = entity.audioPath,
        durationMs = entity.durationMs,
        status = runCatching { BookStatus.valueOf(entity.status) }
            .getOrDefault(BookStatus.IMPORTED),
        createdAt = entity.createdAt,
    )

    fun toEntity(domain: AudioBook) = AudioBookEntity(
        id = domain.id,
        title = domain.title,
        sourceUri = domain.sourceUri,
        audioPath = domain.audioPath,
        durationMs = domain.durationMs,
        status = domain.status.name,
        createdAt = domain.createdAt,
    )

    fun toEntity(dto: AudioBookDto) = AudioBookEntity(
        id = dto.id,
        title = dto.title,
        sourceUri = dto.sourceUrl,
        audioPath = dto.audioUrl,
        durationMs = 0L,
        status = if (dto.audioUrl != null) BookStatus.READY.name else BookStatus.IMPORTED.name,
        createdAt = System.currentTimeMillis(),
    )
}