package com.example.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.app.data.local.dao.AudioBookDao
import com.example.app.data.local.entity.AudioBookEntity

@Database(
    entities = [AudioBookEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AudioBookDatabase : RoomDatabase() {
    abstract fun audioBookDao(): AudioBookDao
}