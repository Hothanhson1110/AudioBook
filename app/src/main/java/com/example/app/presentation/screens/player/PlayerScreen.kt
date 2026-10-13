package com.example.app.presentation.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app.presentation.theme.NeonCyan
import com.example.app.presentation.theme.NeonMagenta
import com.example.app.presentation.theme.NeonPurple
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

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
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

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = NeonCyan)
                }
                return@Box
            }

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
                        state.error.orEmpty(),
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
                // ========= TOP BAR =========
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

                // ========= ARTWORK =========
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
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
                            brush = Brush.linearGradient(listOf(NeonCyan, NeonMagenta)),
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

                Spacer(Modifier.height(20.dp))

                // ========= TITLE =========
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
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Đang xử lý ${state.streamingProgress}% · Nghe trước",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ========= SEEK =========
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
                    Text(
                        formatTime(state.durationMs),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ========= CONTROLS =========
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = viewModel::toggleShuffle) {
                        Icon(
                            Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (state.isShuffleOn) NeonPurple else TextMuted,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    IconButton(onClick = { viewModel.skip(-15_000) }) {
                        Icon(
                            Icons.Default.Replay10,
                            contentDescription = "Lùi 15s",
                            tint = TextSecondary,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    IconButton(onClick = viewModel::prevChapter) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = "Chương trước",
                            tint = TextSecondary,
                            modifier = Modifier.size(36.dp),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple)))
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
                                contentDescription = "Play/Pause",
                                tint = Color.Black,
                                modifier = Modifier.size(42.dp),
                            )
                        }
                    }

                    IconButton(onClick = viewModel::nextChapter) {
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
                            if (state.repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne
                            else Icons.Default.Repeat,
                            contentDescription = "Repeat",
                            tint = if (state.repeatMode != RepeatMode.OFF) NeonPurple
                            else TextMuted,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // ========= BOTTOM TOOLBAR =========
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    SpeedButton(
                        currentSpeed = state.speed,
                        onSelect = viewModel::setSpeed,
                    )
                    SleepTimerButton(
                        remainingMs = state.sleepTimerRemainingMs,
                        onSelect = viewModel::setSleepTimer,
                    )
                }
            }
        }
    }
}

// ============================================================
// Speed dropdown
// ============================================================

@Composable
private fun SpeedButton(
    currentSpeed: Float,
    onSelect: (Float) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f, 2.5f, 3f)
    val active = currentSpeed != 1f

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (active) NeonPurple.copy(alpha = 0.15f) else Color.Transparent
                )
                .clickable { expanded = true }
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Icon(
                Icons.Default.Speed,
                contentDescription = "Speed",
                tint = if (active) NeonPurple else TextSecondary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${formatSpeed(currentSpeed)}x",
                style = MaterialTheme.typography.labelSmall,
                color = if (active) NeonPurple else TextMuted,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        ) {
            speeds.forEach { speed ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${formatSpeed(speed)}x",
                            color = if (speed == currentSpeed) NeonCyan
                            else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (speed == currentSpeed)
                                FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    onClick = {
                        onSelect(speed)
                        expanded = false
                    },
                    leadingIcon = if (speed == currentSpeed) {
                        {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = NeonCyan,
                            )
                        }
                    } else null,
                )
            }
        }
    }
}

// ============================================================
// Sleep timer dropdown
// ============================================================

@Composable
private fun SleepTimerButton(
    remainingMs: Long,
    onSelect: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val active = remainingMs > 0

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (active) NeonPurple.copy(alpha = 0.15f) else Color.Transparent
                )
                .clickable { expanded = true }
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Icon(
                Icons.Default.Bedtime,
                contentDescription = "Sleep timer",
                tint = if (active) NeonPurple else TextSecondary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (active) "${remainingMs / 60_000}ph" else "Hẹn giờ",
                style = MaterialTheme.typography.labelSmall,
                color = if (active) NeonPurple else TextMuted,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        ) {
            listOf(5, 10, 15, 30, 60, 90).forEach { minutes ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "$minutes phút",
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    onClick = {
                        onSelect(minutes)
                        expanded = false
                    },
                )
            }
            if (active) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                DropdownMenuItem(
                    text = {
                        Text(
                            "Tắt hẹn giờ",
                            color = StatusFailed,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = StatusFailed,
                        )
                    },
                    onClick = {
                        onSelect(0)
                        expanded = false
                    },
                )
            }
        }
    }
}

// ============================================================
// Helpers
// ============================================================

private fun formatTime(ms: Long): String {
    if (ms < 0) return "00:00"
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s)
    else "%02d:%02d".format(m, s)
}

private fun formatSpeed(speed: Float): String {
    return if (speed == speed.toInt().toFloat()) {
        speed.toInt().toString()
    } else {
        "%.2f".format(speed).trimEnd('0').trimEnd('.')
    }
}
