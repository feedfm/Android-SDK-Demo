package fm.feed.android.simpledemo.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.feed.android.simpledemo.model.RadioStation
import fm.feed.android.simpledemo.ui.theme.FrTheme

@Composable
fun StationRow(
    station: RadioStation,
    index: Int,
    isActive: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isActive) FrTheme.surface2 else FrTheme.surface1)
            .border(
                BorderStroke(1.dp, if (isActive) FrTheme.accent.copy(alpha = 0.55f) else FrTheme.hair),
                shape,
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        ArtworkView(
            station = station,
            seed = index + 1,
            bars = 7,
            cornerRadius = 14,
            modifier = Modifier
                .padding(start = 6.dp)
                .size(58.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = station.name,
                color = FrTheme.ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            station.subheader?.let { sub ->
                Text(
                    text = sub,
                    color = FrTheme.ink2,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Icon(
            imageVector = if (isActive && isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = if (isActive && isPlaying) "Pause" else "Play",
            tint = FrTheme.accent,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(22.dp),
        )
    }
}
