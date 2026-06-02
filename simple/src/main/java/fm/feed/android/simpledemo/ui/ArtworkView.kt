package fm.feed.android.simpledemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import fm.feed.android.simpledemo.audio.Waveform
import fm.feed.android.simpledemo.model.RadioStation
import fm.feed.android.simpledemo.ui.theme.FrTheme

/**
 * Station artwork: a remote background image when available, otherwise a brand gradient
 * with a static bar-graph waveform.
 */
@Composable
fun ArtworkView(
    station: RadioStation,
    seed: Int,
    bars: Int,
    cornerRadius: Int,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(cornerRadius.dp)
    Box(modifier = modifier.clip(shape)) {
        val url = station.backgroundImageUrl
        if (url != null) {
            SubcomposeAsyncImage(
                model = url,
                contentDescription = station.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = { Fallback(station, seed, bars) },
                error = { Fallback(station, seed, bars) },
            )
        } else {
            Fallback(station, seed, bars)
        }
    }
}

@Composable
private fun Fallback(station: RadioStation, seed: Int, bars: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FrTheme.artworkGradient(station.gradient))
    ) {
        WaveformBars(heights = Waveform.barHeights(seed = seed, bars = bars))
    }
}
