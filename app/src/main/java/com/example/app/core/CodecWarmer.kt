package com.example.app.core

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object CodecWarmer {

    private const val TAG = "CodecWarmer"

    /** Warm up AAC encoder 1 lần để lần đầu encode không bị chậm 8 phút. */
    suspend fun warmUp(cacheDir: File) = withContext(Dispatchers.IO) {
        try {
            val format = MediaFormat.createAudioFormat(
                MediaFormat.MIMETYPE_AUDIO_AAC, 22050, 1,
            ).apply {
                setInteger(MediaFormat.KEY_AAC_PROFILE, 2)
                setInteger(MediaFormat.KEY_BIT_RATE, 64_000)
            }
            val codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
            codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            codec.start()
            // Encode 1 frame trống để load native lib
            val inputIdx = codec.dequeueInputBuffer(100_000)
            if (inputIdx >= 0) {
                codec.queueInputBuffer(
                    inputIdx, 0, 0, 0,
                    MediaCodec.BUFFER_FLAG_END_OF_STREAM,
                )
            }
            codec.stop()
            codec.release()
            Log.i(TAG, "Codec warm-up OK")
        } catch (t: Throwable) {
            Log.w(TAG, "Codec warm-up failed: ${t.message}")
        }
    }
}a
