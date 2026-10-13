package com.example.app.presentation.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.app.presentation.theme.NeonCyan
import com.example.app.presentation.theme.NeonGreen
import com.example.app.presentation.theme.NeonMagenta
import com.example.app.presentation.theme.NeonPurple
import com.example.app.presentation.theme.NeonYellow
import com.example.app.presentation.theme.StatusFailed
import com.example.app.presentation.theme.TextMuted
import com.example.app.presentation.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Neon radial glow background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                NeonPurple.copy(alpha = 0.18f),
                                Color.Transparent,
                            ),
                            radius = 1400f,
                        )
                    )
            )

            // Loading state
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = NeonCyan)
                }
                return@Box
            }

            // Error state
            if (state.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "⚠",
                        style = MaterialTheme.typography.displayMedium,
                        color = StatusFailed,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        state.error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(24.dp))
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = NeonCyan,
                        )
                    }
                }
                return@Box
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // =========================================================
                // TOP BAR
                // =========================================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = viewModel::toggleBookmark) {
                        Icon(
                            if (state.isBookmarked) Icons.Default.Bookmark
                            else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (state.isBookmarked) NeonMagenta
                            else TextSecondary,
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // =========================================================
                // ARTWORK
                // =========================================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    NeonPurple.copy(alpha = 0.35f),
                                    NeonMagenta.copy(alpha = 0.22f),
                                    NeonCyan.copy(alpha = 0.3f),
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                listOf(NeonCyan, NeonMagenta)
                            ),
                            shape = RoundedCornerShape(24.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(110.dp),
                        tint = Color.White.copy(alpha = 0.9f),
                    )

                    if (state.isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            color = NeonCyan,
                            strokeWidth = 3.dp,
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // =========================================================
                // TITLE + CHAPTER
                // =========================================================
                Text(
                    state.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    if (state.isStreamingMode) {
                        "Chunk ${state.currentChapter}/${state.totalChapters.coerceAtLeast(1)}"
                    } else {
                        "Chương ${state.currentChapter}/${state.chapters.size.coerceAtLeast(1)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )

                // =========================================================
                // STREAMING BADGE (chỉ hiện khi đang PROCESSING)
                // =========================================================
                if (state.isStreamingMode) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .border(
                                1.dp,
                                NeonCyan.copy(alpha = 0.4f),
                                RoundedCornerShape(12.dp),
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = NeonCyan,
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            "Đang xử lý ${state.streamingProgress}% · Nghe trước",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // =========================================================
                // SEEK BAR
                // =========================================================
                Slider(
                    value = state.positionMs.toFloat().coerceAtLeast(0f),
                    onValueChange = { viewModel.seekTo(it.toLong()) },
                    valueRange = 0f..(state.durationMs.coerceAtLeast(1L)).toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        formatTime(state.positionMs),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                    )
                    if (state.sleepTimerRemainingMs > 0) {
                        Text(
                            "⏱ ${formatTime(state.sleepTimerRemainingMs)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonYellow,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(
                        formatTime(state.durationMs),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                    )
                }

                Spacer(Modifier.height(20.dp))

                // =========================================================
                // MAIN CONTROLS
                // =========================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Shuffle
                    IconButton(onClick = viewModel::toggleShuffle) {
                        Icon(
                            Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (state.isShuffleOn) NeonPurple else TextMuted,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    // Rewind 15s
                    IconButton(onClick = { viewModel.skip(-15_000) }) {
                        Icon(
                            Icons.Default.Replay10,
                            contentDescription = "Lùi 15s",
                            tint = TextSecondary,
                            modifier = Modifier.size(32.dp),
                        )
                    }

                    // Previous chapter
                    IconButton(onClick = viewModel::prevChapter) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = "Chương trước",
                            tint = TextSecondary,
                            modifier = Modifier.size(36.dp),
                        )
                    }

                    // PLAY/PAUSE — big glowing button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonCyan, NeonPurple)
                                )
                            )
                            .border(
                                2.dp,
                                NeonCyan.copy(alpha = 0.7f),
                                CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconButton(
                            onClick = viewModel::togglePlayPause,
                            modifier = Modifier.size(76.dp),
                        ) {
                            Icon(
                                if (state.isPlaying) Icons.Default.Pause
                                else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.Black,
                                modifier = Modifier.size(42.dp),
                            )
                        }
                    }

                    // Next chapter
                    IconButton(onClick = viewModel::nextChapter) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = "Chương sau",
                            tint = TextSecondary,
                            modifier = Modifier.size(36.dp),
                        )
                    }

                    // Forward 15s
                    IconButton(onClick = { viewModel.skip(15_000) }) {
                        Icon(
                            Icons.Default.Forward10,
                            contentDescription = "Tới 15s",
                            tint = TextSecondary,
                            modifier = Modifier.size(32.dp),
                        )
                    }

                    // Repeat
                    IconButton(onClick = viewModel::cycleRepeat) {
                        Icon(
                            when (state.repeatMode) {
                                RepeatMode.ONE -> Icons.Default.RepeatOne
                                else -> Icons.Default.Repeat
                            },
                            contentDescription = "Repeat",
                            tint = if (state.repeatMode != RepeatMode.OFF)
                                NeonPurple else TextMuted,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // =========================================================
                // BOTTOM TOOLBAR
                // =========================================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    BottomAction(
                        icon = Icons.Default.Speed,
                        label = "${formatSpeed(state.speed)}x",
                        onClick = viewModel::cycleSpeed,
                        active = state.speed != 1f,
                    )
                    BottomAction(
                        icon = Icons.Default.Bedtime,
                        label = if (state.sleepTimerRemainingMs > 0)
                            "${state.sleepTimerRemainingMs / 60_000}ph"
                        else "Hẹn giờ",
                        onClick = { viewModel.setSleepTimer(15) },
                        active = state.sleepTimerRemainingMs > 0,
                    )
                }
            }
        }
    }
}

// =============================================================================
// Helper composables
// =============================================================================

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
                       Icons.Default.Replay10,
                            contentDescription = "Lùi 15s",
                            tint = TextSecondary,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    IconButton(onClick = { viewModel.skip(-10_000) }) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = "Chương trước",
                            tint = TextSecondary,
                            modifier = Modifier.size(36.dp),
                        )
                    }

                    // BIG PLAY/PAUSE with glow
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonCyan, NeonPurple)
                                )
                            )
                            .border(2.dp, NeonCyan.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconButton(
                            onClick = viewModel::togglePlayPause,
                            modifier = Modifier.size(80.dp),
                        ) {
                            Icon(
                                if (state.isPlaying) Icons.Default.Pause
                                else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.Black,
                                modifier = Modifier.size(44.dp),
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.skip(10_000) }) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = "Chương sau",
                            tint = TextSecondary,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                    IconButton(onClick = { viewModel.skip(15_000) }) {
                        Icon(
                            Icons.Default.Forward10,
                            contentDescription = "Tới 15s",
                            tint = TextSecondary,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    IconButton(onClick = viewModel::cycleRepeat) {
                        Icon(
                            when (state.repeatMode) {
                                RepeatMode.ONE -> Icons.Default.RepeatOne
                                else -> Icons.Default.Repeat
                            },
                            contentDescription = "Repeat",
                            tint = if (state.repeatMode != RepeatMode.OFF)
                                NeonPurple else TextMuted,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // BOTTOM TOOLBAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    BottomAction(
                        icon = Icons.Default.Speed,
                        label = "${state.speed}x",
                        onClick = viewModel::cycleSpeed,
                        active = state.speed != 1f,
                    )
                    BottomAction(
                        icon = Icons.Default.Bedtime,
                        label = if (state.sleepTimerRemainingMs > 0)
                            "${state.sleepTimerRemainingMs / 60_000}ph"
                        else "Hẹn giờ",
                        onClick = { viewModel.setSleepTimer(15) },
                        active = state.sleepTimerRemainingMs > 0,
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun BottomAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        IconButton(onClick = onClick) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (active) NeonPurple else TextSecondary,
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) NeonPurple else TextMuted,
        )
    }
}

private fun formatTime(ms: Long): String {
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}
