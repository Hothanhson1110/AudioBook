package com.example.app.data.worker

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal object WavToAacEncoder {

    private const val MIME_TYPE = MediaFormat.MIMETYPE_AUDIO_AAC
    private const val TIMEOUT_US = 10_000L

    fun encode(wavFiles: List<File>, outputFile: File) {
        require(wavFiles.isNotEmpty()) { "No input files" }
        val (sampleRate, channelCount) = readWavHeader(wavFiles.first())

        val format = MediaFormat.createAudioFormat(MIME_TYPE, sampleRate, channelCount).apply {
            setInteger(MediaFormat.KEY_AAC_PROFILE, 2)
            setInteger(MediaFormat.KEY_BIT_RATE, 64_000)
            setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 16 * 1024)
        }

        val encoder = MediaCodec.createEncoderByType(MIME_TYPE)
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        encoder.start()

        val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        var trackIndex = -1
        var muxerStarted = false
        val bufferInfo = MediaCodec.BufferInfo()
        var totalPtsUs = 0L

        try {
            for (wav in wavFiles) {
                val pcm = readWavPcm(wav)
                var offset = 0
                while (offset < pcm.size) {
                    val inputIdx = encoder.dequeueInputBuffer(TIMEOUT_US)
                    if (inputIdx >= 0) {
                        val inBuf = encoder.getInputBuffer(inputIdx)!!
                        inBuf.clear()
                        val size = minOf(inBuf.remaining(), pcm.size - offset)
                        inBuf.put(pcm, offset, size)
                        encoder.queueInputBuffer(inputIdx, 0, size, totalPtsUs, 0)
                        offset += size
                        totalPtsUs += (size.toLong() * 1_000_000L) /
                            (sampleRate.toLong() * 2L * channelCount)
                    }
                }
            }

            val eosIdx = encoder.dequeueInputBuffer(TIMEOUT_US)
            if (eosIdx >= 0) {
                encoder.queueInputBuffer(eosIdx, 0, 0, totalPtsUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
            }

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
        } finally {
            runCatching { encoder.stop() }
            runCatching { encoder.release() }
            if (muxerStarted) runCatching { muxer.stop() }
            runCatching { muxer.release() }
        }
    }

    private fun readWavHeader(wav: File): Pair<Int, Int> {
        val header = ByteArray(44)
        wav.inputStream().use { it.read(header) }
        val bb = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
        val channels = bb.getShort(22).toInt()
        val sampleRate = bb.getInt(24)
        return sampleRate to channels
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
            ) return i + 8
        }
        return 44
    }
}