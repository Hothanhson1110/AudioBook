package com.example.app.data.repository

import android.content.Context
import app.cash.turbine.test
import com.example.app.data.local.dao.AudioBookDao
import com.example.app.data.local.entity.AudioBookEntity
import com.example.app.data.mapper.AudioBookMapper
import com.example.app.data.parser.DocumentParserFactory
import com.example.app.data.remote.ApiService
import com.example.app.domain.model.BookStatus
import com.example.app.domain.repository.ImportProgress
import com.example.app.domain.service.DocumentParser
import com.example.app.domain.service.TextToSpeechService
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class AudioBookRepositoryImplTest {

    private lateinit var dao: AudioBookDao
    private lateinit var parserFactory: DocumentParserFactory
    private lateinit var parser: DocumentParser
    private lateinit var repo: AudioBookRepositoryImpl
    private val context: Context = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        dao = mockk(relaxed = true)
        parser = mockk()
        parserFactory = mockk()
        repo = AudioBookRepositoryImpl(
            dao = dao,
            api = mockk<ApiService>(relaxed = true),
            parserFactory = parserFactory,
            tts = mockk<TextToSpeechService>(relaxed = true),
            mapper = AudioBookMapper(),
            context = context,
        )
    }

    @Test
    @DisplayName("observeBooks map entity → domain")
    fun observeBooks_mapsEntities() = runTest {
        val entities = listOf(
            AudioBookEntity(1, "A", "uri://a.pdf", null, 0, BookStatus.IMPORTED.name, 1L),
        )
        every { dao.observeAll() } returns flowOf(entities)

        repo.observeBooks().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("A", list.first().title)
            assertEquals(BookStatus.IMPORTED, list.first().status)
            awaitComplete()
        }
    }

    @Test
    @DisplayName("importDocument fail khi extension không hỗ trợ")
    fun import_unsupported_emitsFailed() = runTest {
        every { parserFactory.isSupported("txt") } returns false

        repo.importDocument("uri://x.txt", "X").test {
            val item = awaitItem()
            assertTrue(item is ImportProgress.Failed)
            awaitComplete()
        }
    }

    @Test
    @DisplayName("importDocument upsert thành công")
    fun import_success_emitsCompleted() = runTest {
        every { parserFactory.isSupported("pdf") } returns true
        coEvery { dao.upsert(any()) } returns 42L

        repo.importDocument("uri://x.pdf", "X").test {
            val item = awaitItem()
            assertEquals(42L, (item as ImportProgress.Completed).bookId)
            awaitComplete()
        }
        coVerify { dao.upsert(any()) }
    }
}