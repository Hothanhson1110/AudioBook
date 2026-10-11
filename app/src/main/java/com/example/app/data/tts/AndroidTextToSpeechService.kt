package com.example.app.data.tts

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.app.domain.service.SynthesisProgress
import com.example.app.domain.service.TextToSpeechService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidTextToSpeechService @Inject constructor(
    @ApplicationContext private val context: Context,
) : TextToSpeechService {

    private var engine: TextToSpeech? = null
    private val initMutex = Mutex()

    private suspend fun awaitEngine(): TextToSpeech = initMutex.withLock {
        engine?.let { return it }
        val tts = TextToSpeech(context) { /* no-op; chờ result bên dưới */ }
        // Blocking init
        var initialized = false
        tts.setOnUtteranceProgressListener(null)
        // Dùng listener tạm để biết init xong
        val ready = CompletableDeferred<Int>()
        val wrapper = TextToSpeech(context) { status -> ready.complete(status) }
        val status = ready.await() // simplistic; see note below
        if (status != TextToSpeech.SUCCESS) error("TTS init failed")
        engine = wrapper
        wrapper
    }

    override fun synthesize(text: String, outputFile: File): Flow<SynthesisProgress> = callbackFlow {
        val tts = try {
            awaitEngine()
        } catch (t: Throwable) {
            trySend(SynthesisProgress.Failed(t.message ?: "TTS init error"))
            close(); return@callbackFlow
        }

        tts.language = Locale.getDefault()
        outputFile.parentFile?.mkdirs()

        val chunks = text.chunkedBySentence(MAX_CHUNK_CHARS)
        if (chunks.isEmpty()) {
            trySend(SynthesisProgress.Failed("Không có nội dung để đọc"))
            close(); return@callbackFlow
        }

        trySend(SynthesisProgress.Progress(0))

        chunks.forEachIndexed { index, chunk ->
            val utteranceId = "chunk_$index"
            val done = CompletableDeferred<Unit>()

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(id: String?) = Unit
                override fun onDone(id: String?) { done.complete(Unit) }
                @Deprecated("Deprecated in Java")
                override fun onError(id: String?) {
                    done.completeExceptionally(IllegalStateException("TTS onError: $id"))
                }
                override fun onError(id: String?, errorCode: Int) {
                    done.completeExceptionally(IllegalStateException("TTS err $errorCode"))
                }
            })

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            }
            // Ghi tất cả chunk vào 1 file duy nhất (TTS ghi nối tiếp không hỗ trợ),
            // nên dùng file tạm cho từng chunk rồi merge WAV.
            val chunkFile = File(outputFile.parentFile, "${outputFile.nameWithoutExtension}_$index.wav")
            val result = tts.synthesizeToFile(chunk, params, chunkFile, utteranceId)
            if (result != TextToSpeech.SUCCESS) {
                trySend(SynthesisProgress.Failed("synthesizeToFile thất bại ở chunk $index"))
                close(); return@callbackFlow
            }
            done.await()
            chunkFiles += chunkFile
            trySend(SynthesisProgress.Progress(((index + 1) * 100) / chunks.size))
        }

        try {
            mergeWavs(chunkFiles, outputFile)
            chunkFiles.forEach { it.delete() }
            trySend(SynthesisProgress.Completed(outputFile))
        } catch (t: Throwable) {
            trySend(SynthesisProgress.Failed(t.message ?: "Merge WAV failed"))
        }
        close()
        awaitClose { chunkFiles.forEach { runCatching { it.delete() } } }
    }

    private val chunkFiles = mutableListOf<File>()

    override fun stop() {
        engine?.stop()
    }

    // ---- Helpers ----

    private fun String.chunkedBySentence(max: Int): List<String> {
        val sentences = split(Regex("(?<=[.!?…])\\s+"))
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        for (s in sentences) {
            if (sb.length + s.length > max && sb.isNotEmpty()) {
                result += sb.toString().trim()
                sb.clear()
            }
            sb.append(s).append(' ')
        }
        if (sb.isNotBlank()) result += sb.toString().trim()
        return result
    }

    /**
     * Ghép nhiều file WAV cùng format thành 1 file.
     * Chỉ giữ header của file đầu, cộng dồn data chunk và cập nhật size fields.
     */
    private fun mergeWavs(inputs: List<File>, output: File) {
        require(inputs.isNotEmpty()) { "no input" }
        val first = inputs.first()
        val header = ByteArray(44)
        first.inputStream().use { it.read(header) }

        var totalDataSize = 0
        val dataChunks = inputs.map { f ->
            val bytes = f.readBytes()
            val dataStart = findDataOffset(bytes)
            val data = bytes.copyOfRange(dataStart, bytes.size)
            totalDataSize += data.size
            data
        }

        val outHeader = header.copyOf()
        writeIntLE(outHeader, 4, totalDataSize + 36)   // RIFF size
        writeIntLE(outHeader, 40, totalDataSize)       // data size

        output.outputStream().use { out ->
            out.write(outHeader)
            dataChunks.forEach(out::write)
        }
    }

    private fun findDataOffset(bytes: ByteArray): Int {
        // Tìm chuỗi "data" — thường ở offset 36 với PCM WAV chuẩn
        for (i in 12 until bytes.size - 4) {
            if (bytes[i] == 'd'.code.toByte() && bytes[i + 1] == 'a'.code.toByte() &&
                bytes[i + 2] == 't'.code.toByte() && bytes[i + 3] == 'a'.code.toByte()
            ) return i + 8
        }
        return 44
    }

    private fun writeIntLE(arr: ByteArray, offset: Int, value: Int) {
        arr[offset]     = (value and 0xFF).toByte()
        arr[offset + 1] = ((value shr 8) and 0xFF).toByte()
        arr[offset + 2] = ((value shr 16) and 0xFF).toByte()
        arr[offset + 3] = ((value shr 24) and 0xFF).toByte()
    }

    companion object {
        private const val MAX_CHUNK_CHARS = 3000
    }
}