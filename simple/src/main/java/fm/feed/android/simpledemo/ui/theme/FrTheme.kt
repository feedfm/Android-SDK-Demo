package fm.feed.android.simpledemo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset

/** feed radio visual tokens. */
object FrTheme {
    val screenBG = Color(0xFF04141E)
    val surface1 = Color(0xFF08263A)
    val surface2 = Color(0xFF0C344E)
    val accent = Color(0xFF61B978)
    val accentSoft = accent.copy(alpha = 0.16f)
    val dislike = Color(0xFFFF8A77)

    val ink = Color.White.copy(alpha = 0.92f)
    val ink2 = Color.White.copy(alpha = 0.60f)
    val ink3 = Color.White.copy(alpha = 0.38f)
    val hair = Color.White.copy(alpha = 0.08f)

    /** Parses a `#RRGGBB` hex string; returns [Color.Transparent] on malformed input. */
    fun colorFromHex(hex: String): Color {
        val s = hex.removePrefix("#")
        if (s.length != 6) return Color.Transparent
        val value = s.toLongOrNull(16) ?: return Color.Transparent
        val r = ((value shr 16) and 0xFF).toInt()
        val g = ((value shr 8) and 0xFF).toInt()
        val b = (value and 0xFF).toInt()
        return Color(r, g, b)
    }

    /** Diagonal (top-left -> bottom-right) gradient for fallback artwork. */
    fun artworkGradient(hexes: List<String>): Brush =
        Brush.linearGradient(
            colors = hexes.map { colorFromHex(it) },
            start = Offset(0f, 0f),
            end = Offset.Infinite,
        )
}

/** Dark Material3 theme using the feed radio palette. */
@Composable
fun SimpleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = FrTheme.accent,
            onPrimary = FrTheme.screenBG,
            background = FrTheme.screenBG,
            onBackground = FrTheme.ink,
            surface = FrTheme.surface1,
            onSurface = FrTheme.ink,
            surfaceVariant = FrTheme.surface2,
            error = FrTheme.dislike,
        ),
        content = content,
    )
}
