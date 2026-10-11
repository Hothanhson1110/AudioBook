// ObserveBooksUseCase.kt
package com.example.app.domain.usecase

import com.example.app.domain.model.AudioBook
import com.example.app.domain.repository.AudioBookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBooksUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    operator fun invoke(): Flow<List<AudioBook>> = repository.observeBooks()
}

// GetBookUseCase.kt
package com.example.app.domain.usecase

import com.example.app.domain.model.AudioBook
import com.example.app.domain.repository.AudioBookRepository
import javax.inject.Inject

class GetBookUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    suspend operator fun invoke(id: Long): AudioBook? = repository.getBook(id)
}

// ImportDocumentUseCase.kt
package com.example.app.domain.usecase

import com.example.app.domain.repository.AudioBookRepository
import com.example.app.domain.repository.ImportProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImportDocumentUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    operator fun invoke(uriString: String, title: String): Flow<ImportProgress> =
        repository.importDocument(uriString, title)
}

// SynthesizeBookUseCase.kt
package com.example.app.domain.usecase

import com.example.app.domain.repository.AudioBookRepository
import com.example.app.domain.service.SynthesisProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SynthesizeBookUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    operator fun invoke(bookId: Long): Flow<SynthesisProgress> =
        repository.synthesize(bookId)
}

// DeleteBookUseCase.kt
package com.example.app.domain.usecase

import com.example.app.domain.repository.AudioBookRepository
import javax.inject.Inject

class DeleteBookUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deleteBook(id)
}