package com.example.app.data.worker

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltWorker
class TtsChunkWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val bookId = inputData.getLong(KEY_BOOK_ID, -1L)
        val index = inputData.getInt(KEY_INDEX, -1)
        val text = inputData.getString(KEY_TEXT) ?: return@withContext Result.failure()
        val voiceName = inputData.getString(KEY_VOICE) ?: ""
        val localeTag = inputData.getString(KEY_LOCALE) ?: "vi-VN"
        val pitch = inputData.getFloat(KEY_PITCH, 1f)
        val rate = inputData.getFloat(KEY_RATE, 1f)

        if (bookId <= 0L || index < 0 || text.isBlank()) {
            return@withContext Result.failure()
        }

        val outDir = File(applicationContext.filesDir, "audio/book_$bookId").apply { mkdirs() }
        val outFile = File(outDir, "chunk_%05d.wav".format(index))

        try {
            synthesizeToFile(
                text = text,
                voiceName = voiceName,
                localeTag = localeTag,
                pitch = pitch,
                rate = rate,
                outFile = outFile,
            )
            Result.success(workDataOf(KEY_OUTPUT to outFile.absolutePath))
        } catch (t: Throwable) {
            if (runAttemptCount < MAX_RETRY) Result.retry()
            else Result.failure(workDataOf(KEY_ERROR to (t.message ?: "unknown")))
        }
    }

    private suspend fun synthesizeToFile(
        text: String,
        voiceName: String,
        localeTag: String,
        pitch: Float,
        rate: Float,
        outFile: File,
    ) = suspendCancellableCoroutine<Unit> { cont ->
        var tts: TextToSpeech? = null
        var done = false

        tts = TextToSpeech(applicationContext) { status ->
            if (done) return@TextToSpeech
            if (status != TextToSpeech.SUCCESS) {
                done = true
                if (cont.isActive) {
                    cont.resumeWithException(IllegalStateException("TTS init failed: $status"))
                }
                return@TextToSpeech
            }

            val engine = tts ?: return@TextToSpeech
            engine.setPitch(pitch)
            engine.setSpeechRate(rate)

            val voices = engine.voices.orEmpty()
            val picked = voices.firstOrNull { it.name == voiceName }
                ?: voices.firstOrNull {
                    it.locale.toLanguageTag() == localeTag &&
                        !it.isNetworkConnectionRequired
                }
                ?: voices.firstOrNull { it.locale.toLanguageTag() == localeTag }

            if (picked != null) engine.voice = picked
            else engine.language = Locale.forLanguageTag(localeTag)

            engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) = Unit
                override fun onDone(utteranceId: String?) {
                    if (!done && cont.isActive) {
                        done = true
                        cont.resume(Unit)
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    if (!done && cont.isActive) {
                        done = true
                        cont.resumeWithException(IllegalStateException("TTS error"))
                    }
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    if (!done && cont.isActive) {
                        done = true
                        cont.resumeWithException(
                            IllegalStateException("TTS error: $errorCode")
                        )
                    }
                }
            })

            val result = engine.synthesizeToFile(text, null, outFile, "chunk_$text".hashCode().toString())
            if (result != TextToSpeech.SUCCESS && !done && cont.isActive) {
                done = true
                cont.resumeWithException(
                    IllegalStateException("synthesizeToFile failed: $result")
                )
            }
        }

        cont.invokeOnCancellation {
            done = true
            runCatching { tts?.stop() }
            runCatching { tts?.shutdown() }
        }
    }

    companion object {
        const val KEY_BOOK_ID = "book_id"
        const val KEY_INDEX = "index"
        const val KEY_TEXT = "text"
        const val KEY_VOICE = "voice"
        const val KEY_LOCALE = "locale"
        const val KEY_PITCH = "pitch"
        const val KEY_RATE = "rate"
        const val KEY_OUTPUT = "output"
        const val KEY_ERROR = "error"
        private const val MAX_RETRY = 2
    }
}