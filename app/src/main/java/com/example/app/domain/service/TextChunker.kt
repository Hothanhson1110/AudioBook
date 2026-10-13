package com.example.app.domain.service

import android.icu.text.BreakIterator
import android.os.Build
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextChunker @Inject constructor() {

    fun chunk(
        text: String,
        locale: Locale = Locale("vi", "VN"),
        maxChars: Int = DEFAULT_MAX,
    ): List<String> {
        val paragraphs = text.split(Regex("\\n\\s*\\n"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val chunks = mutableListOf<String>()
        val buffer = StringBuilder()

        for (para in paragraphs) {
            val sentences = splitSentences(para, locale)
            for (sentence in sentences) {
                if (buffer.length + sentence.length + 1 > maxChars && buffer.isNotEmpty()) {
                    chunks += buffer.toString().trim()
                    buffer.clear()
                }
                if (sentence.length > maxChars) {
                    sentence.chunked(maxChars).forEach { chunks += it.trim() }
                } else {
                    buffer.append(sentence).append(' ')
                }
            }
            if (buffer.isNotEmpty()) {
                chunks += buffer.toString().trim()
                buffer.clear()
            }
        }
        if (buffer.isNotEmpty()) chunks += buffer.toString().trim()
        return chunks
    }

    private fun splitSentences(paragraph: String, locale: Locale): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val iterator = BreakIterator.getSentenceInstance(locale)
            iterator.setText(paragraph)
            buildList {
                var start = iterator.first()
                var end = iterator.next()
                while (end != BreakIterator.DONE) {
                    val sentence = paragraph.substring(start, end).trim()
                    if (sentence.isNotEmpty()) add(sentence)
                    start = end
                    end = iterator.next()
                }
            }
        } else {
            paragraph.split(Regex("(?<=[.!?…])\\s+")).filter { it.isNotBlank() }
        }
    }

    companion object {
        private const val DEFAULT_MAX = 2800
    }
}