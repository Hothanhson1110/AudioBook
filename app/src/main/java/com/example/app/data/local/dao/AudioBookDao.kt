package com.example.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app.data.local.entity.AudioBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioBookDao {

    @Query("SELECT * FROM audio_books ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<AudioBookEntity>>

    @Query("SELECT * FROM audio_books WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AudioBookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AudioBookEntity): Long

    @Query("UPDATE audio_books SET audioPath = :path, status = :status, durationMs = :duration WHERE id = :id")
    suspend fun updateAudio(id: Long, path: String?, status: String, duration: Long)

    @Delete
    suspend fun delete(entity: AudioBookEntity)
}