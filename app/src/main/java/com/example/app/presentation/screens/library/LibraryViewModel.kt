// LibraryViewModel.kt
package com.example.app.presentation.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.model.BookStatus
import com.example.app.domain.usecase.ObserveBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    observeBooks: ObserveBooksUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeBooks()
            .map { books -> books.filter { it.status == BookStatus.READY } }
            .onEach { ready -> _uiState.value = LibraryUiState.Success(ready) }
            .launchIn(viewModelScope)
    }
}