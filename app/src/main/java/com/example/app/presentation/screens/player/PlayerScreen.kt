package com.example.app.presentation.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.radialGradient(
                        listOf(NeonPurple.copy(alpha = 0.18f), Color.Transparent),
                        radius = 1400f,
                    )
                )
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCyan)
                }
                return@Box
            }

            if (state.error != null) {
                Column(
                    Modifier.fillMaxSize().padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("⚠", style = MaterialTheme.typography.displayMedium,
                        color = StatusFailed)
                    Spacer(Modifier.height(16.dp))
                    Text(state.error!!, color = TextSecondary,
                        textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back",
                            tint = NeonCyan)
                    }
                }
                return@Box
            }

            Column(
                Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // TOP BAR
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = viewModel::toggleBookmark) {
                        Icon(
                            if (state.isBookmarked) Icons.Default.Bookmark
                            else Icons.Default.BookmarkBorder,
                            "Bookmark",
                            tint = if (state.isBookmarked) NeonMagenta
                            else TextSecondary,
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ARTWORK
                Box(
                    Modifier.fillMaxWidth().height(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(listOf(
                            NeonPurple.copy(alpha = 0.35f),
                            NeonMagenta.copy(alpha = 0.22f),
                            NeonCyan.copy(alpha = 0.3f),
                        )))
                        .border(1.dp,
                            Brush.linearGradient(listOf(NeonCyan, NeonMagenta)),
                            RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.MenuBook, null,
                        modifier = Modifier.size(110.dp),
                        tint = Color.White.copy(alpha = 0.9f))
                    if (state.isBuffering) {
                        CircularProgressIndicator(
                            Modifier.size(40.dp).align(Alignment.BottomEnd)
                                .padding(16.dp),
                            color = NeonCyan, strokeWidth = 3.dp)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(state.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(6.dp))
                Text(
                    if (state.isStreamingMode)
                        "Chunk ${state.currentChapter}/${state.totalChapters.coerceAtLeast(1)}"
                    else
                        "Chương ${state.currentChapter}/${state.chapters.size.coerceAtLeast(1)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )

                if (state.isStreamingMode) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f),
                                RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            Modifier.size(14.dp), color = NeonCyan,
                            strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Đang xử lý ${state.streamingProgress}% · Nghe trước",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // SEEK BAR
                Slider(
                    value = state.positionMs.toFloat().coerceAtLeast(0f),
                    onValueChange = { viewModel.seekTo(it.toLong()) },
                    valueRange = 0f..state.durationMs.coerceAtLeast(1L).toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                )
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatTime(state.positionMs),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary)
                    Text(formatTime(state.durationMs),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary)
                }

                Spacer(Modifier.height(20.dp))

                // CONTROLS
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = viewModel::toggleShuffle) {
                        Icon(Icons.Default.Shuffle, "Shuffle",
                            tint = if (state.isShuffleOn) NeonPurple else TextMuted,
                            modifier = Modifier.size(22.dp))
                    }
                    IconButton(onClick = { viewModel.skip(-15_000) }) {
                        Icon(Icons.Default.Replay10, "Lùi 15s",
                            tint = TextSecondary, modifier = Modifier.size(32.dp))
                    }
                    IconButton(onClick = viewModel::prevChapter) {
                        Icon(Icons.Default.SkipPrevious, "Trước",
                            tint = TextSecondary, modifier = Modifier.size(36.dp))
                    }

                    Box(
                        Modifier.size(76.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(
                                NeonCyan, NeonPurple)))
                            .border(2.dp, NeonCyan.copy(alpha = 0.7f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconButton(
                            onClick = viewModel::togglePlayPause,
                            modifier = Modifier.size(76.dp),
                        ) {
                            Icon(
                                if (state.isPlaying) Icons.Default.Pause
                                else Icons.Default.PlayArrow,
                                "Play/Pause",
                                tint = Color.Black,
                                modifier = Modifier.size(42.dp),
                            )
                        }
                    }

                    IconButton(onClick = viewModel::nextChapter) {
                        Icon(Icons.Default.SkipNext, "Sau",
                            tint = TextSecondary, modifier = Modifier.size(36.dp))
                    }
                    IconButton(onClick = { viewModel.skip(15_000) }) {
                        Icon(Icons.Default.Forward10, "Tới 15s",
                            tint = TextSecondary, modifier = Modifier.size(32.dp))
                    }
                    IconButton(onClick = viewModel::cycleRepeat) {
                        Icon(
                            if (state.repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne
                            else Icons.Default.Repeat,
                            "Repeat",
                            tint = if (state.repeatMode != RepeatMode.OFF) NeonPurple
                            else TextMuted,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Row(
                    Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    BottomAction(Icons.Default.Speed,
                        "${formatSpeed(state.speed)}x",
                        viewModel::cycleSpeed,
                        state.speed != 1f)
                    BottomAction(Icons.Default.Bedtime,
                        if (state.sleepTimerRemainingMs > 0)
                            "${state.sleepTimerRemainingMs / 60_000}ph"
                        else "Hẹn giờ",
                        { viewModel.setSleepTimer(15) },
                        state.sleepTimerRemainingMs > 0)
                }
            }
        }
    }
}

@Composable
private fun BottomAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    active: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (active) NeonPurple.copy(alpha = 0.15f) else Color.Transparent
            )
            .padding(horizontal = 20.dp, vertical = 6.dp),
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, label, tint = if (active) NeonPurple else TextSecondary)
        }
        Text(label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) NeonPurple else TextMuted)
    }
}

private fun formatTime(ms: Long): String {
    if (ms < 0) return "00:00"
    val s = ms / 1000
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, sec)
    else "%02d:%02d".format(m, sec)
}

private fun formatSpeed(speed: Float): String =
    if (speed == speed.toInt().toFloat()) speed.toInt().toString()
    else "%.2f".format(speed).trimEnd('0').trimEnd('.')
