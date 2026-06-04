package fm.feed.android.simpledemo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fm.feed.android.simpledemo.R
import fm.feed.android.simpledemo.model.RadioStation

@Composable
fun StationListScreen(
    stations: List<RadioStation>,
    activeStationId: Int?,
    isPlayerOpen: Boolean,
    isPlaying: Boolean,
    onStationClick: (RadioStation) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 20.dp,
            bottom = if (isPlayerOpen) 96.dp else 40.dp,
        ),
    ) {
        item {
            Image(
                painter = painterResource(R.drawable.feedradio_wordmark_white),
                contentDescription = "feed radio",
                contentScale = ContentScale.Fit,
                alignment = Alignment.CenterStart,
                modifier = Modifier
                    // padding must be the OUTER modifier; if height is applied first,
                    // the vertical padding eats into the 26dp and crushes the image.
                    .padding(vertical = 12.dp)
                    .height(26.dp),
            )
        }
        itemsIndexed(items = stations, key = { _, station -> station.id }) { index, station ->
            StationRow(
                station = station,
                index = index,
                isActive = isPlayerOpen && activeStationId == station.id,
                isPlaying = isPlaying,
                onClick = { onStationClick(station) },
            )
        }
    }
}
