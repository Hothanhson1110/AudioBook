package com.example.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app.data.local.dao.AudioBookDao
import com.example.app.data.local.dao.ChapterDao
import com.example.app.data.local.entity.AudioBookEntity
import com.example.app.data.local.entity.ChapterEntity

@Database(
    entities = [AudioBookEntity::class, ChapterEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class AudioBookDatabase : RoomDatabase() {
    abstract fun audioBookDao(): AudioBookDao
    abstract fun chapterDao(): ChapterDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE audio_books ADD COLUMN voiceProfileId TEXT NOT NULL DEFAULT 'vi_natural'"
                )
                db.execSQL(
                    "ALTER TABLE audio_books ADD COLUMN voiceName TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE audio_books ADD COLUMN pitch REAL NOT NULL DEFAULT 1.0"
                )
                db.execSQL(
                    "ALTER TABLE audio_books ADD COLUMN speechRate REAL NOT NULL DEFAULT 1.0"
                )
                db.execSQL(
                    "ALTER TABLE audio_books ADD COLUMN lastPositionMs INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "ALTER TABLE audio_books ADD COLUMN lastPlayedAt INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS chapters (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        bookId INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        startMs INTEGER NOT NULL,
                        endMs INTEGER NOT NULL,
                        level INTEGER NOT NULL DEFAULT 1,
                        FOREIGN KEY(bookId) REFERENCES audio_books(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_chapters_bookId ON chapters(bookId)")
            }
        }

        val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)
    }
}