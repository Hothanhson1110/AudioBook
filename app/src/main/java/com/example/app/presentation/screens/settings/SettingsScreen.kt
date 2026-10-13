package com.example.app.presentation.screens.settings

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app.domain.model.VoiceAge
import com.example.app.domain.model.VoiceGender
import com.example.app.domain.model.VoiceSettings
import com.example.app.domain.model.VoiceStyle
import com.example.app.domain.service.VoiceComposer
import com.example.app.presentation.theme.NeonCyan
import com.example.app.presentation.theme.NeonGreen
import com.example.app.presentation.theme.NeonMagenta
import com.example.app.presentation.theme.NeonPurple
import com.example.app.presentation.theme.TextMuted
import com.example.app.presentation.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = state.settings

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cai dat giong doc", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Preview card
            item {
                VoicePreviewCard(settings)
            }

            // Gender
            item {
                SectionHeader(
                    icon = Icons.Default.Face,
                    title = "Gioi tinh",
                    color = NeonCyan,
                )
            }
            item {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    VoiceGender.entries.forEachIndexed { index, gender ->
                        SegmentedButton(
                            selected = settings.gender == gender,
                            onClick = { viewModel.onGenderChange(gender) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = VoiceGender.entries.size,
                            ),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = NeonCyan.copy(alpha = 0.2f),
                                activeContentColor = NeonCyan,
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                inactiveContentColor = TextSecondary,
                            ),
                        ) {
                            Text(gender.displayName, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Age
            item {
                SectionHeader(
                    icon = Icons.Default.RecordVoiceOver,
                    title = "Do tuoi",
                    color = NeonMagenta,
                )
            }
            item {
                AgeChipsRow(
                    current = settings.age,
                    onSelect = viewModel::onAgeChange,
                )
            }

            // Style
            item {
                SectionHeader(
                    icon = Icons.Default.Style,
                    title = "Phong cach doc",
                    color = NeonPurple,
                )
            }
            items(VoiceStyle.entries) { style ->
                StyleRow(
                    style = style,
                    selected = settings.style == style,
                    onClick = { viewModel.onStyleChange(style) },
                )
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun VoicePreviewCard(settings: VoiceSettings) {
    val profile = VoiceComposer.compose(settings)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(NeonCyan, NeonPurple)),
                shape = RoundedCornerShape(16.dp),
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Cau hinh hien tai",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                settings.summary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                settings.style.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ParamChip("Pitch", "%.2f".format(profile.pitch), NeonMagenta)
                ParamChip("Speed", "%.2f".format(profile.speechRate), NeonGreen)
                ParamChip("Pause", "${profile.pauseBetweenSentencesMs}ms", NeonPurple)
            }
        }
    }
}

@Composable
private fun ParamChip(label: String, value: String, color: Color) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgeChipsRow(
    current: VoiceAge,
    onSelect: (VoiceAge) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        VoiceAge.entries.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { age ->
                    val selected = age == current
                    Box(modifier = Modifier.weight(1f)) {
                        FilterChip(
                            selected = selected,
                            onClick = { onSelect(age) },
                            label = {
                                Text(
                                    age.displayName,
                                    fontWeight = if (selected) FontWeight.Bold
                                    else FontWeight.Normal,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonMagenta.copy(alpha = 0.2f),
                                selectedLabelColor = NeonMagenta,
                                labelColor = TextSecondary,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = NeonMagenta.copy(alpha = 0.4f),
                                selectedBorderColor = NeonMagenta,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StyleRow(
    style: VoiceStyle,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) NeonPurple
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                NeonPurple.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    style.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) NeonPurple
                    else MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    style.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
            }
            if (selected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = NeonPurple,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
