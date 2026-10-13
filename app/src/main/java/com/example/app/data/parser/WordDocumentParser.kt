package com.example.app.data.parser

import android.content.Context
import android.net.Uri
import android.util.Xml
import com.example.app.domain.service.DocumentParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.xmlpull.v1.XmlPullParser
import java.io.BufferedInputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordDocumentParser @Inject constructor(
    @ApplicationContext private val context: Context,
) : DocumentParser {

    override val supportedExtensions = setOf("docx")

    override fun parse(uriString: String): Flow<String> = flow {
        val uri = Uri.parse(uriString)
        val input = context.contentResolver.openInputStream(uri)
            ?: error("Cannot open file: $uriString")

        input.use { stream ->
            ZipInputStream(BufferedInputStream(stream)).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    if (entry.name == "word/document.xml") {
                        emitParagraphs(zip) { emit(it) }
                        break
                    }
                    entry = zip.nextEntry
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun emitParagraphs(
        xml: java.io.InputStream,
        onParagraph: suspend (String) -> Unit,
    ) {
        val parser = Xml.newPullParser()
        parser.setInput(xml, null)
        val buffer = StringBuilder()
        var event = parser.eventType

        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.TEXT -> buffer.append(parser.text)
                XmlPullParser.END_TAG -> if (parser.name == "p") {
                    val text = buffer.toString().trim()
                    if (text.isNotEmpty()) onParagraph(text)
                    buffer.clear()
                }
            }
            event = parser.next()
        }
    }
}