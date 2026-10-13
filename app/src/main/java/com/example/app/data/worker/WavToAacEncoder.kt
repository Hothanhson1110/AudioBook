package com.example.app.data.worker

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal object WavToAacEncoder {

    private const val TAG = "WavToAacEncoder"
    private const val MIME_TYPE = MediaFormat.MIMETYPE_AUDIO_AAC
    private const val TIMEOUT_US = 10_000L
    private const val MAX_INPUT_SIZE = 32 * 1024

    fun encode(wavFiles: List<File>, outputFile: File) {
        require(wavFiles.isNotEmpty()) { "No input files" }

        val (sampleRate, channelCount, bitsPerSample) = readWavHeader(wavFiles.first())
        Log.i(TAG, "encode: sampleRate=$sampleRate channels=$channelCount bits=$bitsPerSample")

        require(bitsPerSample == 16) {
            "Only 16-bit PCM supported, got $bitsPerSample"
        }

        val format = MediaFormat.createAudioFormat(MIME_TYPE, sampleRate, channelCount).apply {
            setInteger(MediaFormat.KEY_AAC_PROFILE, 2) // AAC_LC
            setInteger(MediaFormat.KEY_BIT_RATE, 64_000)
            setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, MAX_INPUT_SIZE)
        }

        val encoder = MediaCodec.createEncoderByType(MIME_TYPE)
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        encoder.start()

        if (outputFile.exists()) outputFile.delete()
        val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        var trackIndex = -1
        var muxerStarted = false
        val bufferInfo = MediaCodec.BufferInfo()
        var totalPtsUs = 0L
        val bytesPerSample = 2 // 16-bit
        val bytesPerSecond = sampleRate.toLong() * channelCount * bytesPerSample

        try {
            // Feed PCM từ từng file
            for ((fileIdx, wav) in wavFiles.withIndex()) {
                val pcm = readWavPcm(wav)
                Log.i(TAG, "file[$fileIdx] pcm=${pcm.size} bytes")
                var offset = 0

                while (offset < pcm.size) {
                    // Drain output TRƯỚC khi feed (giải phóng buffer)
                    drainOutput(encoder, muxer, bufferInfo, trackIndex).also { result ->
                        if (!muxerStarted && result.trackIndex >= 0) {
                            trackIndex = result.trackIndex
                            muxerStarted = true
                        }
                    }

                    val inputIdx = encoder.dequeueInputBuffer(TIMEOUT_US)
                    if (inputIdx >= 0) {
                        val inBuf = encoder.getInputBuffer(inputIdx)!!
                        inBuf.clear()
                        val size = minOf(inBuf.remaining(), pcm.size - offset)
                        inBuf.put(pcm, offset, size)
                        encoder.queueInputBuffer(inputIdx, 0, size, totalPtsUs, 0)
                        offset += size
                        totalPtsUs += (size.toLong() * 1_000_000L) / bytesPerSecond
                    }
                }
            }

            // Queue EOS
            Log.i(TAG, "queue EOS, totalPtsUs=$totalPtsUs")
            val eosIdx = encoder.dequeueInputBuffer(TIMEOUT_US)
            if (eosIdx >= 0) {
                encoder.queueInputBuffer(
                    eosIdx, 0, 0, totalPtsUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                )
            }

            // Drain hết output còn lại cho đến EOS
            var eos = false
            while (!eos) {
                val outIdx = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
                when {
                    outIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        if (!muxerStarted) {
                            trackIndex = muxer.addTrack(encoder.outputFormat)
                            muxer.start()
                            muxerStarted = true
                        }
                    }
                    outIdx == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                        // Không có gì — tiếp tục chờ
                    }
                    outIdx >= 0 -> {
                        val outBuf = encoder.getOutputBuffer(outIdx)!!
                        if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                            bufferInfo.size = 0
                        }
                        if (bufferInfo.size > 0 && muxerStarted) {
                            outBuf.position(bufferInfo.offset)
                            outBuf.limit(bufferInfo.offset + bufferInfo.size)
                            muxer.writeSampleData(trackIndex, outBuf, bufferInfo)
                        }
                        encoder.releaseOutputBuffer(outIdx, false)
                        if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                            eos = true
                        }
                    }
                }
            }
            Log.i(TAG, "encode DONE: ${outputFile.length()} bytes")
        } finally {
            runCatching { encoder.stop() }
            runCatching { encoder.release() }
            if (muxerStarted) runCatching { muxer.stop() }
            runCatching { muxer.release() }
        }
    }

    /** Drain output có sẵn, không block. Return info để cập nhật trackIndex. */
    private fun drainOutput(
        encoder: MediaCodec,
        muxer: MediaMuxer,
        bufferInfo: MediaCodec.BufferInfo,
        currentTrackIndex: Int,
    ): DrainResult {
        var trackIdx = currentTrackIndex
        var started = currentTrackIndex >= 0

        while (true) {
            val outIdx = encoder.dequeueOutputBuffer(bufferInfo, 0L) // non-blocking
            when {
                outIdx == MediaCodec.INFO_TRY_AGAIN_LATER -> return DrainResult(trackIdx, started)
                outIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    if (!started) {
                        trackIdx = muxer.addTrack(encoder.outputFormat)
                        muxer.start()
                        started = true
                    }
                }
                outIdx >= 0 -> {
                    val outBuf = encoder.getOutputBuffer(outIdx)!!
                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                        bufferInfo.size = 0
                    }
                    if (bufferInfo.size > 0 && started) {
                        outBuf.position(bufferInfo.offset)
                        outBuf.limit(bufferInfo.offset + bufferInfo.size)
                        muxer.writeSampleData(trackIdx, outBuf, bufferInfo)
                    }
                    encoder.releaseOutputBuffer(outIdx, false)
                }
                else -> return DrainResult(trackIdx, started)
            }
        }
    }

    private data class DrainResult(val trackIndex: Int, val muxerStarted: Boolean)

    private fun readWavHeader(wav: File): Triple<Int, Int, Int> {
        val header = ByteArray(44)
        wav.inputStream().use { it.read(header) }
        val bb = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
        val channels = bb.getShort(22).toInt()
        val sampleRate = bb.getInt(24)
        val bitsPerSample = bb.getShort(34).toInt()
        return Triple(sampleRate, channels, bitsPerSample)
    }

    private fun readWavPcm(wav: File): ByteArray {
        val bytes = wav.readBytes()
        val dataOffset = findDataChunk(bytes)
        return bytes.copyOfRange(dataOffset, bytes.size)
    }

    private fun findDataChunk(bytes: ByteArray): Int {
        for (i in 12 until bytes.size - 8) {
            if (bytes[i] == 'd'.code.toByte() &&
                bytes[i + 1] == 'a'.code.toByte() &&
                bytes[i + 2] == 't'.code.toByte() &&
                bytes[i + 3] == 'a'.code.toByte()
            ) {
                // Bỏ qua 4 byte size, data bắt đầu sau
                return i + 8
            }
        }
        return 44
    }
}