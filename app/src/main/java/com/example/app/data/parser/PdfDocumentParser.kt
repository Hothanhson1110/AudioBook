package com.example.app.data.parser

import android.content.Context
import android.net.Uri
import com.example.app.domain.service.DocumentParser
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfDocumentParser @Inject constructor(
    @ApplicationContext private val context: Context,
) : DocumentParser {

    override val supportedExtensions = setOf("pdf")

    override fun parse(uriString: String): Flow<String> = flow {
        PDFBoxResourceLoader.init(context)
        val uri = Uri.parse(uriString)
        val input = context.contentResolver.openInputStream(uri)
            ?: error("Không mở được file: $uriString")

        input.use { stream ->
            PDDocument.load(stream).use { doc ->
                val stripper = PDFTextStripper().apply { sortByPosition = true }
                val raw = stripper.getText(doc)
                raw.split(Regex("\\n\\s*\\n"))
                    .asSequence()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .forEach { emit(it) }
            }
        }
    }.flowOn(Dispatchers.IO)
}