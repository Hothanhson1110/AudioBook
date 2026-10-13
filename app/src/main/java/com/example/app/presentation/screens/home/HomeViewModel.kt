package com.example.app.presentation.screens.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.model.BookStatus
import com.example.app.domain.repository.SynthesisState
import com.example.app.domain.service.ImportProgress
import com.example.app.domain.service.VoiceProfile
import com.example.app.domain.service.VoicePresets
import com.example.app.domain.usecase.DeleteBookUseCase
import com.example.app.domain.usecase.ImportDocumentUseCase
import com.example.app.domain.usecase.ObserveBooksUseCase
import com.example.app.domain.usecase.SynthesizeBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private val _stats = MutableStateFlow(HomeStats())
    val stats = _stats.asStateFlow()

    private val _synthesis = MutableStateFlow<Map<Long, SynthesisState>>(emptyMap())
    val synthesis = _synthesis.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val synthesisJobs = mutableMapOf<Long, Job>()

    init {
        observeBooks()
            .onEach { books ->
                _uiState.value = HomeUiState.Success(books)
                _stats.value = HomeStats(
                    total = books.size,
                    ready = books.count { it.status == BookStatus.READY },
                    processing = books.count { it.status == BookStatus.PROCESSING },
                    failed = books.count { it.status == BookStatus.FAILED },
                )
            }
            .catch { t -> _uiState.value = HomeUiState.Error(t.message ?: "Load failed") }
            .launchIn(viewModelScope)
    }

    fun onFilePicked(uri: Uri, displayName: String? = null) {
        val title = displayName
            ?: uri.lastPathSegment?.substringAfterLast('/')
            ?: "Untitled"

        viewModelScope.launch {
            importDocument(uri.toString(), title).collect { progress ->
                when (progress) {
                    is ImportProgress.Completed -> {
                        _events.send(HomeEvent.Imported(progress.bookId))
                        // Auto navigate sang Player
                        _events.send(HomeEvent.NavigateToPlayer(progress.bookId))
                        // Bắt đầu synthesis luôn
                        onSynthesize(progress.bookId, VoicePresets.VIETNAMESE.first())
                    }
                    is ImportProgress.Failed ->
                        _events.send(HomeEvent.Message(progress.message))
                }
            }
        }
    }

    fun onSynthesize(bookId: Long, profile: VoiceProfile) {
        if (synthesisJobs[bookId]?.isActive == true) return

        synthesisJobs[bookId] = viewModelScope.launch {
            _synthesis.value = _synthesis.value + (bookId to SynthesisState.Running(0))

            synthesizeBook.observeState(bookId)
                .onEach { state ->
                    _synthesis.value = _synthesis.value + (bookId to state)
                    when (state) {
                        is SynthesisState.Completed -> {
                            _synthesis.value = _synthesis.value - bookId
                            synthesisJobs.remove(bookId)
                        }
                        is SynthesisState.Failed -> {
                            _synthesis.value = _synthesis.value - bookId
                            synthesisJobs.remove(bookId)
                            _events.send(HomeEvent.Message(state.message))
                        }
                        else -> Unit
                    }
                }
                .launchIn(viewModelScope)

            runCatching { synthesizeBook.start(bookId, profile) }
                .onFailure { t ->
                    _synthesis.value = _synthesis.value - bookId
                    synthesisJobs.remove(bookId)
                    _events.send(HomeEvent.Message(t.message ?: "Synthesis failed"))
                }
        }
    }

    fun onCancelSynthesis(bookId: Long) {
        synthesisJobs.remove(bookId)?.cancel()
        viewModelScope.launch {
            synthesizeBook.cancel(bookId)
            _synthesis.value = _synthesis.value - bookId
        }
    }

    fun onDelete(bookId: Long) {
        viewModelScope.launch { deleteBook(bookId) }
    }

    fun onDeleteAllFailed() {
        val current = (_uiState.value as? HomeUiState.Success)?.books ?: return
        viewModelScope.launch {
            current.filter { it.status == BookStatus.FAILED }
                .forEach { deleteBook(it.id) }
            _events.send(HomeEvent.Message("Đã xoá các sách lỗi"))
        }
    }
}
