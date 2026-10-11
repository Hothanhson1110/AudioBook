// ReaderViewModel.kt
package com.example.app.presentation.screens.reader

import android.media.MediaPlayer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.usecase.GetBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val getBook: GetBookUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bookId: Long = checkNotNull(savedStateHandle["bookId"])
    private var player: MediaPlayer? = null
    private var tickerJob: Job? = null

    private val _uiState = MutableStateFlow<ReaderUiState>(ReaderUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val book = getBook(bookId)
            _uiState.value = if (book?.audioPath == null) {
                ReaderUiState.NotFound
            } else {
                preparePlayer(book.audioPath)
                ReaderUiState.Ready(book)
            }
        }
    }

    private fun preparePlayer(path: String) {
        player?.release()
        player = MediaPlayer().apply {
            setDataSource(path)
            prepare()
        }
    }

    fun toggle() {
        val s = _uiState.value as? ReaderUiState.Ready ?: return
        val p = player ?: return
        if (p.isPlaying) {
            p.pause(); stopTicker()
        } else {
            p.start(); startTicker()
        }
        _uiState.update { (it as ReaderUiState.Ready).copy(isPlaying = p.isPlaying) }
    }

    fun seekTo(ms: Long) {
        player?.seekTo(ms.toInt())
        _uiState.update { (it as? ReaderUiState.Ready)?.copy(positionMs = ms) ?: it }
    }

    private fun startTicker() {
        stopTicker()
        tickerJob = viewModelScope.launch {
            while (true) {
                val p = player ?: break
                _uiState.update { cur ->
                    (cur as? ReaderUiState.Ready)?.copy(positionMs = p.currentPosition.toLong()) ?: cur
                }
                delay(500)
            }
        }
    }

    private fun stopTicker() { tickerJob?.cancel(); tickerJob = null }

    override fun onCleared() {
        super.onCleared()
        stopTicker()
        player?.release()
        player = null
    }
}