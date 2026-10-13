package com.example.app.presentation.screens.home

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app.domain.model.AudioBook
import com.example.app.domain.model.BookStatus
import com.example.app.domain.repository.SynthesisState
import com.example.app.domain.service.VoicePresets
import com.example.app.presentation.theme.NeonCyan
import com.example.app.presentation.theme.NeonGreen
import com.example.app.presentation.theme.NeonMagenta
import com.example.app.presentation.theme.NeonPurple
import com.example.app.presentation.theme.StatusFailed
import com.example.app.presentation.theme.TextMuted
import com.example.app.presentation.theme.TextSecondary

private val SUPPORTED_MIME_TYPES = arrayOf(
    "application/pdf",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenPlayer: (Long) -> Unit,
    onOpenLibrary: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val synthesis by viewModel.synthesis.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        val name = uri.lastPathSegment?.substringAfterLast('/')
        viewModel.onFilePicked(uri, name)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.Message -> snackbarHostState.showSnackbar(event.text)
                is HomeEvent.Imported ->
                    snackbarHostState.showSnackbar("Đã thêm sách")
                is HomeEvent.NavigateToPlayer -> onOpenPlayer(event.bookId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp),
                        )
                        Spacer(Modifier.size(12.dp))
                        Column {
                            Text(
                                "AudioBook",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                "Thư viện của bạn",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                            )
                        }
                    }
                },
                actions = {
                    if (stats.failed > 0) {
                        IconButton(onClick = viewModel::onDeleteAllFailed) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = "Xoá tất cả lỗi",
                                tint = StatusFailed,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { filePicker.launch(SUPPORTED_MIME_TYPES) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Thêm sách") },
                containerColor = NeonCyan,
                contentColor = Color.Black,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // HEADER — Stats
            StatsRow(stats)

            // BODY — Danh sách sách
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    HomeUiState.Loading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator(color = NeonCyan) }

                    is HomeUiState.Error -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(state.message, color = StatusFailed)
                    }

                    is HomeUiState.Success -> {
                        if (state.books.isEmpty()) {
                            EmptyState()
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp, end = 16.dp,
                                    top = 8.dp, bottom = 100.dp,
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(state.books, key = { it.id }) { book ->
                                    NeonBookCard(
                                        book = book,
                                        state = synthesis[book.id],
                                        onPlay = { onOpenPlayer(book.id) },
                                        onSynthesize = {
                                            viewModel.onSynthesize(
                                                book.id,
                                                VoicePresets.findById(book.voiceProfileId),
                                            )
                                        },
                                        onCancel = { viewModel.onCancelSynthesis(book.id) },
                                        onDelete = { viewModel.onDelete(book.id) },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // FOOTER — Library button
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        Icons.Default.LibraryMusic,
                        contentDescription = null,
                        tint = NeonMagenta,
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        "Thư viện đã sẵn sàng: ${stats.ready}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsRow(stats: HomeStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatChip("Tổng", stats.total, NeonPurple, Modifier.weight(1f))
        StatChip("Sẵn sàng", stats.ready, NeonGreen, Modifier.weight(1f))
        StatChip("Đang xử lý", stats.processing, NeonCyan, Modifier.weight(1f))
        StatChip("Lỗi", stats.failed, StatusFailed, Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(vertical = 10.dp, horizontal = 12.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color,
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
            )
        }
    }
}

@Composable
private fun NeonBookCard(
    book: AudioBook,
    state: SynthesisState?,
    onPlay: () -> Unit,
    onSynthesize: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
) {
    val statusColor = when (book.status) {
        BookStatus.READY -> NeonGreen
        BookStatus.PROCESSING -> NeonCyan
        BookStatus.FAILED -> StatusFailed
        BookStatus.IMPORTED -> NeonMagenta
    }
    val isProcessing = book.status == BookStatus.PROCESSING
    val isReady = book.status == BookStatus.READY && book.audioPath != null
    val isFailed = book.status == BookStatus.FAILED

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(statusColor.copy(alpha = 0.6f), Color.Transparent),
                ),
                shape = RoundedCornerShape(16.dp),
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(statusColor),
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    book.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.size(8.dp))

            Text(
                book.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
            )

            if (state is SynthesisState.Running) {
                Spacer(Modifier.size(12.dp))
                LinearProgressIndicator(
                    progress = { state.progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NeonCyan,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    "Đang tạo audio... ${state.progressPercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                )
            }

            Spacer(Modifier.size(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(
                    onClick = onSynthesize,
                    enabled = !isProcessing && !isReady && !isFailed,
                ) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = "Tạo audio",
                        tint = if (!isProcessing && !isReady && !isFailed)
                            NeonPurple else TextMuted,
                    )
                }
                IconButton(onClick = onPlay, enabled = isReady) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Nghe",
                        tint = if (isReady) NeonGreen else TextMuted,
                    )
                }
                if (isProcessing) {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Cancel, "Huỷ", tint = StatusFailed)
                    }
                } else if (isFailed) {
                    IconButton(onClick = onSynthesize) {
                        Icon(Icons.Default.Refresh, "Thử lại", tint = NeonYellow)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Xoá", tint = StatusFailed)
                    }
                } else {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Xoá", tint = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Default.AutoStories,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = NeonCyan.copy(alpha = 0.4f),
        )
        Spacer(Modifier.size(16.dp))
        Text(
            "Chưa có sách nào",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary,
        )
        Spacer(Modifier.size(4.dp))
        Text(
            "Nhấn \"Thêm sách\" để chọn PDF / Word",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
        )
    }
}

// Helper import
private val NeonYellow = com.example.app.presentation.theme.NeonYellow
