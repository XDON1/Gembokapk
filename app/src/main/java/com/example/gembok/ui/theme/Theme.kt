package com.example.gembok.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = OliveAccentDark,
    onPrimary = DarkCharcoalBg,
    primaryContainer = OliveAccentSubtleDark,
    onPrimaryContainer = DarkWarmText,
    secondary = OliveAccentDark,
    onSecondary = DarkCharcoalBg,
    background = DarkCharcoalBg,
    surface = DarkCharcoalSurface,
    onBackground = DarkWarmText,
    onSurface = DarkWarmText,
    outline = DarkCharcoalBorder,
    outlineVariant = Color(0xFF383E3A),
    surfaceVariant = DarkCharcoalSurfaceElevated,
    onSurfaceVariant = DarkSubtext,
    error = HealthVulnerable,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = OliveAccent,
    onPrimary = Color.White,
    primaryContainer = OliveAccentSubtleLight,
    onPrimaryContainer = OliveAccent,
    secondary = OliveAccent,
    onSecondary = Color.White,
    background = LightWarmBg,
    surface = LightWarmSurface,
    onBackground = LightCharcoalText,
    onSurface = LightCharcoalText,
    outline = LightWarmBorder,
    outlineVariant = Color(0xFFECE7E0),
    surfaceVariant = LightWarmSurfaceElevated,
    onSurfaceVariant = LightSubtext,
    error = HealthVulnerable,
    onError = Color.White
)

@Composable
fun GembokTheme(
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
