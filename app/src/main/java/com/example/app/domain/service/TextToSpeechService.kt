package com.example.app.domain.service

import kotlinx.coroutines.flow.Flow
import java.io.File

interface TextToSpeechService {
    /**
     * Tổng hợp [text] thành file WAV tại [outputFile].
     * Phát [SynthesisProgress] để UI hiển thị tiến độ.
     */
    fun synthesize(text: String, outputFile: File): Flow<SynthesisProgress>
    fun stop()
}

sealed interface SynthesisProgress {
    data class Progress(val percent: Int) : SynthesisProgress
    data class Completed(val file: File) : SynthesisProgress
    data class Failed(val message: String) : SynthesisProgress
}