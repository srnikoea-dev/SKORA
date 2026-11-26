package com.rekapnilai.merdeka.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Warna khas Kurikulum Merdeka / pendidikan Indonesia
val MerdekaBlue = Color(0xFF1565C0)
val MerdekaBlueDark = Color(0xFF0D47A1)
val MerdekaOrange = Color(0xFFFF6F00)
val MerdekaGreen = Color(0xFF2E7D32)
val MerdekaRed = Color(0xFFC62828)
val MerdekaYellow = Color(0xFFF9A825)

private val LightColors = lightColorScheme(
    primary = MerdekaBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = MerdekaBlueDark,
    secondary = MerdekaOrange,
    onSecondary = Color.White,
    tertiary = MerdekaGreen,
    background = Color(0xFFF5F7FA),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    error = MerdekaRed
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = MerdekaBlueDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFFB74D),
    onSecondary = Color.Black,
    tertiary = Color(0xFF81C784),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    error = Color(0xFFEF9A9A)
)

@Composable
fun RekapNilaiMerdekaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
