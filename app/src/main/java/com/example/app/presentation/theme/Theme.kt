package com.example.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeonColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = NeonCyan,

    secondary = NeonMagenta,
    onSecondary = Color.Black,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = NeonMagenta,

    tertiary = NeonPurple,
    onTertiary = Color.White,

    background = DarkBg,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = DarkOutline,
    outlineVariant = DarkOutline,

    error = StatusFailed,
    onError = Color.White,
)

@Composable
fun AudioBookTheme(
    darkTheme: Boolean = true,   // Always dark for neon
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = NeonColorScheme,
        typography = AppTypography,
        content = content,
    )
}
