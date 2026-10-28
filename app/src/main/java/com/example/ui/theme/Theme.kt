package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ChessGold,
    onPrimary = Color(0xFF141210),
    primaryContainer = ChessGoldDark,
    onPrimaryContainer = Color(0xFFFFF2D6),
    secondary = ChessGoldLight,
    onSecondary = Color(0xFF141210),
    background = ChessDarkBg,
    onBackground = Color(0xFFEDE8E3),
    surface = ChessSurfaceDark,
    onSurface = Color(0xFFEDE8E3),
    surfaceVariant = ChessCardDark,
    onSurfaceVariant = Color(0xFFD4C9BC),
    outline = ChessBorderDark,
    error = ChessCrimson,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ChessGoldDark,
    onPrimary = Color.White,
    primaryContainer = ChessGoldLight,
    onPrimaryContainer = Color(0xFF261904),
    secondary = ChessGold,
    onSecondary = Color.Black,
    background = Color(0xFFF9F7F4),
    onBackground = Color(0xFF1F1C18),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F1C18),
    surfaceVariant = Color(0xFFEDE6DD),
    onSurfaceVariant = Color(0xFF4A453F),
    outline = Color(0xFFC7BCB0),
    error = ChessCrimson,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to rich luxury dark theme for chess
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
