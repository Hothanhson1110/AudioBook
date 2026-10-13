package com.example.app.domain.service

import java.io.File

sealed interface SynthesisProgress {
    data class Progress(val percent: Int) : SynthesisProgress
    data class Completed(val file: File) : SynthesisProgress
    data class Failed(val message: String) : SynthesisProgress
}

sealed interface ImportProgress {
    data class Completed(val bookId: Long) : ImportProgress
    data class Failed(val message: String) : ImportProgress
}