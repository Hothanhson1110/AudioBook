package com.example.app.data.parser

import com.example.app.domain.service.DocumentParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentParserFactory @Inject constructor(
    parsers: Set<@JvmSuppressWildcards DocumentParser>,
) {
    private val registry: Map<String, DocumentParser> = parsers
        .flatMap { parser -> parser.supportedExtensions.map { it.lowercase() to parser } }
        .toMap()

    fun parserFor(extension: String): DocumentParser? =
        registry[extension.lowercase()]

    fun isSupported(extension: String): Boolean =
        registry.containsKey(extension.lowercase())
}