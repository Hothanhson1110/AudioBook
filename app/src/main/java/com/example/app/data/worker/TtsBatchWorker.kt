package com.example.app.data.worker

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.app.core.WorkerLogger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Worker xử lý MỘT BATCH gồm nhiều chunk tuần tự.
 * Giảm số worker từ 468 → 4 (hoặc 8) → tránh overwhelm JobScheduler.
 */
@HiltWorker
class TtsBatchWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val bookId = inputData.getLong(KEY_BOOK_ID, -1L)
        val batchIndex = inputData.getInt(KEY_BATCH_INDEX, -1)
        val texts = inputData.getStringArray(KEY_TEXTS) ?: return@withContext Result.failure()
        val startIndex = inputData.getInt(KEY_START_INDEX, -1)
        val voiceName = inputData.getString(KEY_VOICE) ?: ""
        val localeTag = inputData.getString(KEY_LOCALE) ?: "vi-VN"
        val pitch = inputData.getFloat(KEY_PITCH, 1f)
        val rate = inputData.getFloat(KEY_RATE, 1f)

        if (bookId <= 0L || batchIndex < 0 || texts.isEmpty()) {
            return@withContext Result.failure()
        }

        WorkerLogger.log(
            applicationContext, "TtsBatch#$batchIndex",
            "start: ${texts.size} chunks (idx $startIndex..${startIndex + texts.size - 1})"
        )

        val outDir = File(applicationContext.filesDir, "audio/book_$bookId").apply { mkdirs() }

        try {
            var tts = initTts(voiceName, localeTag, pitch, rate)

            try {
                texts.forEachIndexed { i, text ->
                    val actualIndex = startIndex + i
                    val outFile = File(outDir, "chunk_%05d.wav".format(actualIndex))

                    if (outFile.exists() && outFile.length() > 0) {
                        WorkerLogger.log(
                            applicationContext, "TtsBatch#$batchIndex",
                            "skip #$actualIndex (đã có)"
                        )
                        return@forEachIndexed
                    }

                    synthesizeOne(tts, text, outFile, actualIndex)

                    // Restart TTS mỗi 10 chunk để tránh memory leak
                    if (i > 0 && i % 10 == 0) {
                        runCatching { tts.stop() }
                        runCatching { tts.shutdown() }
                        tts = initTts(voiceName, localeTag, pitch, rate)
                    }
                }
            } finally {
                runCatching { tts.stop() }
                runCatching { tts.shutdown() }
            }

            WorkerLogger.log(
                applicationContext, "TtsBatch#$batchIndex",
                "DONE ${texts.size} chunks"
            )
            Result.success()
        } catch (t: Throwable) {
            WorkerLogger.logError(applicationContext, "TtsBatch#$batchIndex", t)
            if (runAttemptCount < MAX_RETRY) Result.retry()
            else Result.failure(workDataOf(KEY_ERROR to (t.message ?: "unknown")))
        }
    }

    private suspend fun initTts(
        voiceName: String,
        localeTag: String,
        pitch: Float,
        rate: Float,
    ): TextToSpeech = suspendCancellableCoroutine { cont ->
        var tts: TextToSpeech? = null
        var done = false

        tts = TextToSpeech(applicationContext) { status ->
            if (done) return@TextToSpeech
            if (status != TextToSpeech.SUCCESS) {
                done = true
                if (cont.isActive) cont.resumeWithException(
                    IllegalStateException("TTS init failed: $status")
                )
                return@TextToSpeech
            }

            val engine = tts ?: return@TextToSpeech
            engine.setPitch(pitch)
            engine.setSpeechRate(rate)

            val voices = engine.voices.orEmpty()
            val picked = voices.firstOrNull { it.name == voiceName }
                ?: voices.firstOrNull {
                    it.locale.toLanguageTag() == localeTag && !it.isNetworkConnectionRequired
                }
                ?: voices.firstOrNull { it.locale.toLanguageTag() == localeTag }

            if (picked != null) engine.voice = picked
            else engine.language = Locale.forLanguageTag(localeTag)

            done = true
            if (cont.isActive) cont.resume(engine)
        }

        cont.invokeOnCancellation {
            done = true
            runCatching { tts?.stop() }
            runCatching { tts?.shutdown() }
        }
    }

    private suspend fun synthesizeOne(
        tts: TextToSpeech,
        text: String,
        outFile: File,
        index: Int,
    ) = suspendCancellableCoroutine<Unit> { cont ->
        var done = false
        val utteranceId = "chunk_$index"

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) = Unit
            override fun onDone(id: String?) {
                if (!done && cont.isActive) {
                    done = true
                    cont.resume(Unit)
                }
            }
            @Deprecated("Deprecated")
            override fun onError(id: String?) {
                if (!done && cont.isActive) {
                    done = true
                    cont.resumeWithException(IllegalStateException("TTS onError"))
                }
            }
            override fun onError(id: String?, errorCode: Int) {
                if (!done && cont.isActive) {
                    done = true
                    cont.resumeWithException(IllegalStateException("TTS error: $errorCode"))
                }
            }
        })

        val result = tts.synthesizeToFile(text, null, outFile, utteranceId)
        if (result != TextToSpeech.SUCCESS && !done && cont.isActive) {
            done = true
            cont.resumeWithException(IllegalStateException("synthesizeToFile: $result"))
        }

        cont.invokeOnCancellation { done = true }
    }

    companion object {
        const val KEY_BOOK_ID = "book_id"
        const val KEY_BATCH_INDEX = "batch_index"
        const val KEY_START_INDEX = "start_index"
        const val KEY_TEXTS = "texts"
        const val KEY_VOICE = "voice"
        const val KEY_LOCALE = "locale"
        const val KEY_PITCH = "pitch"
        const val KEY_RATE = "rate"
        const val KEY_ERROR = "error"
        private const val MAX_RETRY = 1
    }
}
