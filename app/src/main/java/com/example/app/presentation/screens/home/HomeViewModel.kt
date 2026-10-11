package com.example.app.presentation.screens.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.repository.ImportProgress
import com.example.app.domain.service.SynthesisProgress
import com.example.app.domain.usecase.DeleteBookUseCase
import com.example.app.domain.usecase.ImportDocumentUseCase
import com.example.app.domain.usecase.ObserveBooksUseCase
import com.example.app.domain.usecase.SynthesizeBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeBooks: ObserveBooksUseCase,
    private val importDocument: ImportDocumentUseCase,
    private val synthesizeBook: SynthesizeBookUseCase,
    private val deleteBook: DeleteBookUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _synthesis = MutableStateFlow<SynthesisUiState?>(null)
    val synthesis = _synthesis.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeBooks()
            .onEach { books -> _uiState.value = HomeUiState.Success(books) }
            .catch { t -> _uiState.value = HomeUiState.Error(t.message ?: "Lỗi tải dữ liệu") }
            .launchIn(viewModelScope)
    }

    fun onFilePicked(uri: Uri, displayName: String? = null) {
        val title = displayName
            ?: uri.lastPathSegment?.substringAfterLast('/')
            ?: "Không tên"

        viewModelScope.launch {
            importDocument(uri.toString(), title).collect { progress ->
                when (progress) {
                    is ImportProgress.Completed -> {
                        _events.send(HomeEvent.Imported(progress.bookId))
                        onSynthesize(progress.bookId)
                    }
                    is ImportProgress.Failed ->
                        _events.send(HomeEvent.Message(progress.message))
                }
            }
        }
    }

    fun onSynthesize(bookId: Long) {
        viewModelScope.launch {
            _synthesis.value = SynthesisUiState(bookId, 0)
            synthesizeBook(bookId).collect { p ->
                when (p) {
                    is SynthesisProgress.Progress ->
                        _synthesis.value = SynthesisUiState(bookId, p.percent)
                    is SynthesisProgress.Completed ->
                        _synthesis.value = null
                    is SynthesisProgress.Failed -> {
                        _synthesis.value = null
                        _events.send(HomeEvent.Message(p.message))
                    }
                }
            }
        }
    }

    fun onDelete(bookId: Long) {
        viewModelScope.launch { deleteBook(bookId) }
    }
}