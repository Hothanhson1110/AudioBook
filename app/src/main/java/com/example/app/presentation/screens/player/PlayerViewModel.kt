package com.example.app.presentation.screens.player

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.app.domain.model.BookStatus
import com.example.app.domain.repository.AudioBookRepository
import com.example.app.domain.usecase.GetBookUseCase
import com.example.app.domain.usecase.UpdatePlaybackUseCase
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getBook: GetBookUseCase,
    private val updatePlayback: UpdatePlaybackUseCase,
    private val repository: AudioBookRepository,
    private val sessionToken: SessionToken,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bookId: Long = checkNotNull(savedStateHandle["bookId"])
    private var controller: MediaController? = null
    private var tickerJob: Job? = null
    private var streamingJob: Job? = null
    private var sleepJob: Job? = null
    private var lastSavedPosition = 0L
    private var isStreaming = false
    private var loadedChunkCount = 0

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val book = getBook(bookId) ?: run {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Không tìm thấy sách",
                )
                return@launch
            }

            // Quyết định mode: file hoàn chỉnh vs streaming chunks
            val isReady = book.status == BookStatus.READY && book.audioPath != null
            val isStreamingMode = !isReady && book.status == BookStatus.PROCESSING

            val initialItems: List<MediaItem> = if (isReady) {
                listOf(
                    MediaItem.Builder()
                        .setUri(book.audioPath)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(book.title)
                                .setIsBrowsable(false)
                                .setIsPlayable(true)
                                .build()
                        )
                        .build()
                )
            } else if (isStreamingMode) {
                val chunks = repository.getAvailableChunks(bookId)
                if (chunks.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Chưa có audio (đang chờ xử lý)",
                    )
                    return@launch
                }
                isStreaming = true
                loadedChunkCount = chunks.size
                chunks.map { f ->
                    MediaItem.Builder()
                        .setUri(android.net.Uri.fromFile(f))
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(book.title)
                                .setIsBrowsable(false)
                                .setIsPlayable(true)
                                .build()
                        )
                        .build()
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Sách chưa có audio",
                )
                return@launch
            }

            val future = MediaController.Builder(context, sessionToken).buildAsync()
            future.addListener({
                try {
                    val c = future.get()
                    controller = c
                    c.addListener(playerListener)
                    c.setMediaItems(initialItems)
                    c.prepare()
                    c.seekTo(book.lastPositionMs)
                    c.playWhenReady = true

                    _uiState.value = _uiState.value.copy(
                        title = book.title,
                        durationMs = book.durationMs,
                        isLoading = false,
                        isStreamingMode = isStreaming,
                        streamingProgress = if (isStreaming) {
                            // Sẽ update từ flow observer bên dưới
                            _uiState.value.streamingProgress
                        } else 100,
                        totalChapters = initialItems.size.coerceAtLeast(1),
                    )
                    startTicker()

                    if (isStreaming) startStreamingWatcher()
                } catch (t: Throwable) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = t.message ?: "Lỗi player",
                    )
                }
            }, MoreExecutors.directExecutor())
        }
    }

    /**
     * Trong streaming mode: theo dõi synthesis progress + bổ sung chunks mới khi có.
     */
    private fun startStreamingWatcher() {
        streamingJob?.cancel()
        streamingJob = viewModelScope.launch {
            repository.observeSynthesisState(bookId)
                .onEach { state ->
                    when (state) {
                        is com.example.app.domain.repository.SynthesisState.Running -> {
                            _uiState.value = _uiState.value.copy(
                                streamingProgress = state.progressPercent,
                            )
                            // Load thêm chunks nếu có
                            val chunks = repository.getAvailableChunks(bookId)
                            if (chunks.size > loadedChunkCount) {
                                val c = controller ?: return@onEach
                                val currentIdx = c.currentMediaItemIndex
                                val currentPos = c.currentPosition
                                val newItems = chunks.map { f ->
                                    MediaItem.Builder()
                                        .setUri(android.net.Uri.fromFile(f))
                                        .setMediaMetadata(
                                            MediaMetadata.Builder()
                                                .setTitle(_uiState.value.title)
                                                .setIsBrowsable(false)
                                                .setIsPlayable(true)
                                                .build()
                                        )
                                        .build()
                                }
                                c.setMediaItems(newItems, currentIdx, currentPos)
                                loadedChunkCount = chunks.size
                                _uiState.value = _uiState.value.copy(
                                    totalChapters = chunks.size,
                                )
                            }
                        }
                        is com.example.app.domain.repository.SynthesisState.Completed -> {
                            // Synthesis xong → switch sang m4a (nếu user chưa nghe hết)
                            val book = getBook(bookId)
                            if (book?.audioPath != null) {
                                val c = controller ?: return@onEach
                                val currentIdx = c.currentMediaItemIndex
                                val currentPos = c.currentPosition
                                // Chỉ switch nếu còn đang phát sớm
                                if (currentIdx < 2) {
                                    val item = MediaItem.Builder()
                                        .setUri(book.audioPath)
                                        .setMediaMetadata(
                                            MediaMetadata.Builder()
                                                .setTitle(book.title)
                                                .setIsBrowsable(false)
                                                .setIsPlayable(true)
                                                .build()
                                        )
                                        .build()
                                    c.setMediaItem(item)
                                    c.prepare()
                                    c.seekTo(book.lastPositionMs.coerceAtLeast(currentPos))
                                    isStreaming = false
                                    _uiState.value = _uiState.value.copy(
                                        isStreamingMode = false,
                                        streamingProgress = 100,
                                        totalChapters = 1,
                                        currentChapter = 1,
                                    )
                                }
                            }
                        }
                        else -> Unit
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                val c = controller ?: break
                val position = c.currentPosition
                _uiState.value = _uiState.value.copy(
                    positionMs = position,
                    durationMs = c.duration.coerceAtLeast(0),
                    isPlaying = c.isPlaying,
                    isBuffering = c.playbackState == Player.STATE_BUFFERING,
                    currentChapter = if (isStreaming) c.currentMediaItemIndex + 1
                    else 1,
                )
                if (kotlin.math.abs(position - lastSavedPosition) > 5000) {
                    lastSavedPosition = position
                    updatePlayback(bookId, position)
                }
                delay(500)
            }
        }
    }

    fun togglePlayPause() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun skip(deltaMs: Long) {
        val c = controller ?: return
        c.seekTo((c.currentPosition + deltaMs).coerceAtLeast(0))
    }

    fun seekTo(ms: Long) {
        controller?.seekTo(ms)
    }

    fun nextChapter() {
        controller?.seekToNextMediaItem()
    }

    fun prevChapter() {
        controller?.seekToPreviousMediaItem()
    }

    fun cycleSpeed() {
        val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f, 3f)
        val current = _uiState.value.speed
        val next = speeds[(speeds.indexOf(current) + 1).mod(speeds.size)]
        controller?.setPlaybackSpeed(next)
        _uiState.value = _uiState.value.copy(speed = next)
    }

    fun cycleRepeat() {
        val next = when (_uiState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        controller?.repeatMode = when (next) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
        _uiState.value = _uiState.value.copy(repeatMode = next)
    }

    fun toggleShuffle() {
        val c = controller ?: return
        val next = !_uiState.value.isShuffleOn
        c.shuffleModeEnabled = next
        _uiState.value = _uiState.value.copy(isShuffleOn = next)
    }

    fun toggleBookmark() {
        _uiState.value = _uiState.value.copy(
            isBookmarked = !_uiState.value.isBookmarked
        )
    }

    fun setSleepTimer(minutes: Int) {
        sleepJob?.cancel()
        if (minutes <= 0) {
            _uiState.value = _uiState.value.copy(sleepTimerRemainingMs = 0L)
            return
        }
        sleepJob = viewModelScope.launch {
            var remaining = minutes * 60_000L
            while (remaining > 0) {
                _uiState.value = _uiState.value.copy(sleepTimerRemainingMs = remaining)
                delay(1000)
                remaining -= 1000
            }
            controller?.pause()
            _uiState.value = _uiState.value.copy(sleepTimerRemainingMs = 0L)
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            if (state == Player.STATE_ENDED) {
                _uiState.value = _uiState.value.copy(isPlaying = false)
            }
        }
    }

    override fun onCleared() {
        val position = controller?.currentPosition ?: 0L
        val c = controller
        controller = null
        tickerJob?.cancel()
        sleepJob?.cancel()
        streamingJob?.cancel()
        c?.removeListener(playerListener)
        c?.release()

        viewModelScope.launch { runCatching { updatePlayback(bookId, position) } }
        super.onCleared()
    }
}
