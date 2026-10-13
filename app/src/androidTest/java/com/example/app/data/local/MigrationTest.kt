package com.example.app.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val dbName = "migration-test.db"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AudioBookDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun migrate1To2() {
        helper.createDatabase(dbName, 1).apply {
            execSQL(
                "INSERT INTO audio_books (title, sourceUri, audioPath, durationMs, status, createdAt) " +
                    "VALUES ('Test', 'uri://x.pdf', NULL, 0, 'IMPORTED', 1000)"
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(
            dbName, 2, true, AudioBookDatabase.MIGRATION_1_2
        )

        db.query("SELECT voiceProfileId, pitch, lastPositionMs FROM audio_books").use { c ->
            c.moveToFirst()
            assert(c.getString(0) == "vi_natural")
            assert(c.getFloat(1) == 1.0f)
            assert(c.getLong(2) == 0L)
        }
    }
}