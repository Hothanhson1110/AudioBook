// ReaderScreen.kt
package com.example.app.presentation.screens.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ReaderScreen(
    bookId: Long,
    onBack: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (val s = state) {
            ReaderUiState.Loading -> Text("Đang tải…")
            ReaderUiState.NotFound -> Text("Chưa có bản audio. Hãy tạo từ màn hình chính.")
            is ReaderUiState.Ready -> {
                Text(s.book.title, style = MaterialTheme.typography.headlineSmall)
                Text("${s.positionMs / 1000}s / ${s.book.durationMs / 1000}s")
                Button(onClick = viewModel::toggle) {
                    Text(if (s.isPlaying) "Tạm dừng" else "Phát")
                }
            }
        }
        Button(onClick = onBack) { Text("Quay lại") }
    }
}