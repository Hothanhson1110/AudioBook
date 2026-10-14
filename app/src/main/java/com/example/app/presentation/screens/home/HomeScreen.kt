package com.example.app.presentation.screens.home

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app.domain.model.AudioBook
import com.example.app.domain.model.BookStatus
import com.example.app.presentation.theme.*

private val MIME_TYPES = arrayOf(
    "application/pdf",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenPlayer: (Long) -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val synthesis by viewModel.synthesis.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        viewModel.onFilePicked(uri, uri.lastPathSegment?.substringAfterLast('/'))
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { e ->
            when (e) {
                is HomeEvent.Message -> snackbar.showSnackbar(e.text)
                is HomeEvent.Imported -> snackbar.showSnackbar("Da them sach")
                is HomeEvent.NavigateToPlayer -> onOpenPlayer(e.bookId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoStories, null,
                            tint = NeonCyan, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("AudioBook",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold)
                            Text("Thu vien cua ban",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted)
                        }
                    }
                },
                actions = {
                    if (stats.failed > 0) {
                        IconButton(onClick = viewModel::onDeleteAllFailed) {
                            Icon(Icons.Default.DeleteSweep, "Xoa loi",
                                tint = StatusFailed)
                        }
                    }
                    IconButton(
                        onClick = { picker.launch(MIME_TYPES) },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(CircleShape)
                            .background(NeonCyan),
                    ) {
                        Icon(Icons.Default.Add, "Them sach", tint = Color.Black)
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(NeonMagenta),
                    ) {
                        Icon(Icons.Default.Settings, "Cai dat", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            StatsRow(stats)

            Box(Modifier.weight(1f)) {
                when (val s = uiState) {
                    HomeUiState.Loading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator(color = NeonCyan) }

                    is HomeUiState.Error -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) { Text(s.message, color = StatusFailed) }

                    is HomeUiState.Success -> {
                        if (s.books.isEmpty()) EmptyState()
                        else LazyColumn(
                            Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(s.books, key = { it.id }) { book ->
                                BookCard(
                                    book = book,
                                    progress = synthesis[book.id],
                                    onPlay = { onOpenPlayer(book.id) },
                                    onSynth = { viewModel.onResynthesize(book.id) },
                                    onCancel = { viewModel.onCancelSynthesis(book.id) },
                                    onDelete = { viewModel.onDelete(book.id) },
                                )
                            }
                        }
                    }
                }
            }

            Surface(color = MaterialTheme.colorScheme.surface) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.LibraryMusic, null,
                        tint = NeonMagenta, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Da san sang: ${stats.ready} sach",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun StatsRow(stats: HomeStats) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatChip("Tong", stats.total, NeonPurple, Modifier.weight(1f))
        StatChip("San sang", stats.ready, NeonGreen, Modifier.weight(1f))
        StatChip("Xu ly", stats.processing, NeonCyan, Modifier.weight(1f))
        StatChip("Loi", stats.failed, StatusFailed, Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(label: String, value: Int, color: Color, modifier: Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp),
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = color)
            Text(label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun BookCard(
    book: AudioBook,
    progress: SynthesisProgress?,
    onPlay: () -> Unit,
    onSynth: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
) {
    val statusColor = when (book.status) {
        BookStatus.READY -> NeonGreen
        BookStatus.PROCESSING -> NeonCyan
        BookStatus.FAILED -> StatusFailed
        BookStatus.IMPORTED -> NeonMagenta
    }
    val processing = book.status == BookStatus.PROCESSING
    val ready = book.status == BookStatus.READY && book.audioPath != null
    val failed = book.status == BookStatus.FAILED
    val canPreview = progress?.canPreview == true

    Card(
        Modifier.fillMaxWidth().border(
            1.dp,
            Brush.linearGradient(listOf(statusColor.copy(alpha = 0.6f), Color.Transparent)),
            RoundedCornerShape(16.dp),
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(statusColor))
                Spacer(Modifier.width(8.dp))
                Text(book.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor, fontWeight = FontWeight.Bold)
                if (canPreview) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text("Preview",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(book.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2, overflow = TextOverflow.Ellipsis)

            if (progress != null) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress.percent / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NeonCyan,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Dang xu ly ${progress.percent}% (${progress.doneChunks}/${progress.totalChunks})",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                )
            }

            Spacer(Modifier.height(12.dp))

            if (ready || (processing && canPreview)) {
                Button(
                    onClick = onPlay,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (ready) NeonGreen else NeonCyan,
                        contentColor = Color.Black,
                    ),
                ) {
                    Icon(
                        if (ready) Icons.Default.PlayArrow else Icons.Default.Headphones,
                        null, modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (ready) "Nghe ngay" else "Nghe truoc",
                        fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onSynth,
                    enabled = !processing && !ready && !failed) {
                    Icon(Icons.Default.GraphicEq, "Tao audio",
                        tint = if (!processing && !ready && !failed) NeonPurple
                        else TextMuted)
                }
                when {
                    processing -> {
                        IconButton(onClick = onCancel) {
                            Icon(Icons.Default.Cancel, "Huy", tint = StatusFailed)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, "Xoa", tint = TextSecondary)
                        }
                    }
                    failed -> {
                        IconButton(onClick = onSynth) {
                            Icon(Icons.Default.Refresh, "Thu lai", tint = NeonYellow)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, "Xoa", tint = StatusFailed)
                        }
                    }
                    else -> IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Xoa", tint = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Default.AutoStories, null,
            modifier = Modifier.size(80.dp),
            tint = NeonCyan.copy(alpha = 0.4f))
        Spacer(Modifier.height(16.dp))
        Text("Chua co sach nao",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text("Nhan + o goc tren de them sach",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted)
    }
}
