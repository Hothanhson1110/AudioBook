package com.example.app.domain.usecase

import app.cash.turbine.test
import com.example.app.domain.model.AudioBook
import com.example.app.domain.repository.AudioBookRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ObserveBooksUseCaseTest {

    private lateinit var repository: AudioBookRepository
    private lateinit var useCase: ObserveBooksUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = ObserveBooksUseCase(repository)
    }

    @Test
    @DisplayName("emit danh sách audio books từ repository")
    fun `returns books from repository`() = runTest {
        val expected = listOf(
            AudioBook(
                id = 1,
                title = "Clean Code",
                sourceUri = "content://a.pdf",
                audioPath = null,
                durationMs = 1000L,
                status = com.example.app.domain.model.BookStatus.IMPORTED,
                createdAt = 0L,
            ),
        )
        every { repository.observeBooks() } returns flowOf(expected)

        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }
}