package com.example.app.domain.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Locale

class TextChunkerTest {

    private val chunker = TextChunker()

    @Test
    @DisplayName("Chunk empty string returns empty list")
    fun chunkEmpty() {
        assertTrue(chunker.chunk("").isEmpty())
    }

    @Test
    @DisplayName("Chunk splits long text by max chars")
    fun chunkSplitsByMaxChars() {
        val text = (1..100).joinToString(" ") { "Sentence number $it with some extra content." }
        val chunks = chunker.chunk(text, Locale("vi", "VN"), maxChars = 500)
        assertTrue(chunks.isNotEmpty())
        assertTrue(chunks.all { it.length <= 500 })
    }

    @Test
    @DisplayName("Chunk preserves abbreviations like TS.")
    fun chunkPreservesAbbreviations() {
        val text = "TS. Nguyen Van A sinh nam 1980. Ong la chuyen gia."
        val chunks = chunker.chunk(text, Locale("vi", "VN"), maxChars = 2000)
        assertEquals(1, chunks.size)
        assertTrue(chunks[0].contains("TS. Nguyen Van A"))
    }
}