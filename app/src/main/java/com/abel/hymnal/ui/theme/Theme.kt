package com.abel.hymnal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Same design tokens as the CSS :root block. */
object Tokens {
    val Bg = Color(0xFF0B0E1A)
    val Surface = Color(0xFF141827)
    val SurfaceHover = Color(0xFF1A2235)
    val Border = Color(0xFF1F2A42)
    val BorderHi = Color(0xFF2E3F62)
    val Accent = Color(0xFF6D7CF8)
    val AccentDim = Color(0xFF2E357A)
    val AccentText = Color(0xFFC7CCFF)
    val TextPrimary = Color(0xFFE8EAF6)
    val Muted = Color(0xFF8893B8)
    val CardTop = Color(0xFF141827)
    val CardBottom = Color(0xFF0D1020)
}

private val HymnalColors = darkColorScheme(
    primary = Tokens.Accent,
    background = Tokens.Bg,
    surface = Tokens.Surface,
    onPrimary = Color.White,
    onBackground = Tokens.TextPrimary,
    onSurface = Tokens.TextPrimary
)

@Composable
fun HymnalTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // The app is always dark, matching the original design.
    MaterialTheme(colorScheme = HymnalColors, content = content)
}
