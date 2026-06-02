package fm.feed.android.simpledemo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

/**
 * Static bar-graph waveform, positioned along the lower portion of the artwork.
 * [heights] are values in 0..1 (see [fm.feed.android.simpledemo.audio.Waveform]).
 */
@Composable
fun WaveformBars(
    heights: List<Double>,
    modifier: Modifier = Modifier,
    barColor: Color = Color.White.copy(alpha = 0.85f),
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val count = heights.size
        if (count == 0) return@Canvas

        val inset = w * 0.10f
        val areaW = w - inset * 2f
        val areaH = h * 0.38f
        val gap = areaW * 0.07f / count
        val barW = (areaW - gap * (count - 1)) / count

        // Bottom edge of the bar area, inset slightly from the artwork's bottom.
        val areaBottom = h - h * 0.14f
        var x = inset
        for (value in heights) {
            val barH = (areaH * value.toFloat()).coerceAtLeast(1f)
            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, areaBottom - barH),
                size = Size(barW, barH),
                cornerRadius = CornerRadius(2f, 2f),
            )
            x += barW + gap
        }
    }
}
