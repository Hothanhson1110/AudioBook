package com.example.app.data.parser

import android.content.Context
import android.net.Uri
import com.example.app.domain.service.DocumentParser
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfDocumentParser @Inject constructor(
    @ApplicationContext private val context: Context,
) : DocumentParser {

    override val supportedExtensions = setOf("pdf")

    override fun parse(uriString: String): Flow<String> = flow {
        val uri = Uri.parse(uriString)
        val input = context.contentResolver.openInputStream(uri)
            ?: error("Cannot open file: $uriString")

        input.use { stream ->
            PDDocument.load(stream).use { doc ->
                val stripper = PDFTextStripper().apply { sortByPosition = true }
                for (page in 1..doc.numberOfPages) {
                    kotlinx.coroutines.currentCoroutineContext().ensureActive()
                    stripper.startPage = page
                    stripper.endPage = page
                    stripper.getText(doc)
                        .split(Regex("\\n\\s*\\n"))
                        .asSequence()
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .forEach { emit(it) }
                }
            }
        }
    }.flowOn(Dispatchers.IO)
}