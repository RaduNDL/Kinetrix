package com.kinetix.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KinetixColorScheme = darkColorScheme(
    primary = KinetixLime,
    onPrimary = KinetixBackground,
    secondary = KinetixMint,
    background = KinetixBackground,
    onBackground = KinetixText,
    surface = KinetixSurface,
    onSurface = KinetixText,
    surfaceVariant = KinetixSurfaceRaised,
    onSurfaceVariant = KinetixMuted,
    outline = KinetixOutline,
    error = KinetixError,
    onError = Color(0xFF3A0909)
)

@Composable
fun KinetixTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KinetixColorScheme,
        typography = Typography(),
        content = content
    )
}