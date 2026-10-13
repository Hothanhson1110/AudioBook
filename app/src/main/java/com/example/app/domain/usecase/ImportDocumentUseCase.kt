package com.example.app.domain.usecase

import com.example.app.domain.repository.AudioBookRepository
import com.example.app.domain.service.ImportProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImportDocumentUseCase @Inject constructor(
    private val repository: AudioBookRepository,
) {
    operator fun invoke(uriString: String, title: String): Flow<ImportProgress> =
        repository.importDocument(uriString, title)
}