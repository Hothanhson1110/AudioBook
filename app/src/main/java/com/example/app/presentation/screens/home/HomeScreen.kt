package com.example.app.presentation.screens.home

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app.domain.model.AudioBook
import com.example.app.domain.model.BookStatus
import com.example.app.domain.repository.SynthesisState
import com.example.app.domain.service.VoicePresets

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
        viewModel.onFilePicked(uri)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.Message -> snackbarHostState.showSnackbar(event.text)
                is HomeEvent.Imported ->
                    snackbarHostState.showSnackbar("Da them sach vao thu vien")
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("AudioBook") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { filePicker.launch(SUPPORTED_MIME_TYPES) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Them sach") },
            )
        },
    ) { padding ->
        when (val state = uiState) {
            HomeUiState.Loading -> Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) { CircularProgressIndicator() }

            is HomeUiState.Error -> Text(
                text = state.message,
                modifier = Modifier.padding(padding).padding(16.dp),
            )

            is HomeUiState.Success -> {
                if (state.books.isEmpty()) {
                    EmptyState(Modifier.fillMaxSize().padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.books, key = { it.id }) { book ->
                            BookCard(
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
}

@Composable
private fun BookCard(
    book: AudioBook,
    state: SynthesisState?,
    onPlay: () -> Unit,
    onSynthesize: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(book.title, style = MaterialTheme.typography.titleMedium)
            Text("Trang thai: ${book.status}", style = MaterialTheme.typography.bodySmall)

            if (state is SynthesisState.Running) {
                LinearProgressIndicator(
                    progress = { state.progressPercent / 100f },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
                Text(
                    "Dang tao audio... ${state.progressPercent}%",
                    style = MaterialTheme.typography.labelSmall,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onSynthesize) {
                    Icon(Icons.Default.GraphicEq, contentDescription = "Tao audio")
                }
                IconButton(
                    onClick = onPlay,
                    enabled = book.status == BookStatus.READY && book.audioPath != null,
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Nghe")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Xoa")
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Chua co sach nao")
        Text(
            "Nhan \"Them sach\" de chon file PDF / Word",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}