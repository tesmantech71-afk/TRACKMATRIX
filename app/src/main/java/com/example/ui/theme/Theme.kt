package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MatrixCyanPrimary,
    onPrimary = Color(0xFF001F2A),
    primaryContainer = Color(0xFF004D63),
    onPrimaryContainer = Color(0xFFBCE9FF),
    secondary = MatrixBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF142B66),
    onSecondaryContainer = Color(0xFFD6E3FF),
    tertiary = MatrixEmeraldTertiary,
    onTertiary = Color(0xFF00391A),
    tertiaryContainer = Color(0xFF005328),
    onTertiaryContainer = Color(0xFF69FF9E),
    background = MatrixDarkBackground,
    onBackground = MatrixTextPrimary,
    surface = MatrixDarkSurface,
    onSurface = MatrixTextPrimary,
    surfaceVariant = MatrixSurfaceVariant,
    onSurfaceVariant = MatrixTextSecondary,
    error = MatrixAlarmCrimson,
    onError = Color.White,
    outline = MatrixBorder
)

private val LightColorScheme = lightColorScheme(
    primary = MatrixLightPrimary,
    onPrimary = Color.White,
    secondary = Color(0xFF265EA7),
    onSecondary = Color.White,
    background = MatrixLightBackground,
    onBackground = Color(0xFF121B28),
    surface = MatrixLightSurface,
    onSurface = Color(0xFF121B28),
    surfaceVariant = MatrixLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF434E60),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to command center dark matrix mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
