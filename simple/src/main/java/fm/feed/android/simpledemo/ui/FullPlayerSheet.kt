package fm.feed.android.simpledemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.feed.android.simpledemo.audio.TimeFormat
import fm.feed.android.simpledemo.model.RadioStation
import fm.feed.android.simpledemo.ui.theme.FrTheme

@Composable
fun FullPlayerSheet(
    station: RadioStation,
    seed: Int,
    title: String,
    artist: String,
    isPlaying: Boolean,
    canSkip: Boolean,
    liked: Boolean,
    disliked: Boolean,
    elapsed: Float,
    remaining: Float?,
    progress: Float,
    onTogglePlay: () -> Unit,
    onSkip: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleDislike: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "NOW PLAYING",
            color = FrTheme.ink3,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
        )
        Text(
            text = station.name,
            color = FrTheme.ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(24.dp))

        ArtworkView(
            station = station,
            seed = seed,
            bars = 11,
            cornerRadius = 24,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .fillMaxWidth()
                .aspectRatio(1f),
        )

        Spacer(Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                color = FrTheme.ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = artist,
                color = FrTheme.ink2,
                fontSize = 16.sp,
            )
        }

        Spacer(Modifier.height(18.dp))

        ProgressBar(progress = progress, modifier = Modifier.fillMaxWidth().height(14.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text(TimeFormat.mmss(elapsed), color = FrTheme.ink2, fontSize = 12.sp)
            Spacer(Modifier.weight(1f))
            Text(
                text = remaining?.let { "-" + TimeFormat.mmss(it) } ?: "--:--",
                color = FrTheme.ink2,
                fontSize = 12.sp,
            )
        }

        Spacer(Modifier.height(22.dp))

        Controls(
            isPlaying = isPlaying,
            canSkip = canSkip,
            liked = liked,
            disliked = disliked,
            onTogglePlay = onTogglePlay,
            onSkip = onSkip,
            onToggleLike = onToggleLike,
            onToggleDislike = onToggleDislike,
        )

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Swipe down to minimize",
            color = FrTheme.ink3,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun Controls(
    isPlaying: Boolean,
    canSkip: Boolean,
    liked: Boolean,
    disliked: Boolean,
    onTogglePlay: () -> Unit,
    onSkip: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleDislike: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        SideButton(
            icon = Icons.Filled.ThumbDown,
            contentDescription = "Dislike",
            tint = if (disliked) FrTheme.dislike else FrTheme.ink2,
            background = if (disliked) FrTheme.dislike.copy(alpha = 0.14f) else Color.Transparent,
            onClick = onToggleDislike,
        )

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(FrTheme.accent),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onTogglePlay, modifier = Modifier.size(72.dp)) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = FrTheme.screenBG,
                    modifier = Modifier.size(34.dp),
                )
            }
        }

        IconButton(onClick = onSkip, enabled = canSkip, modifier = Modifier.size(56.dp)) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "Skip",
                tint = if (canSkip) FrTheme.ink else FrTheme.ink3,
                modifier = Modifier.size(30.dp),
            )
        }

        SideButton(
            icon = Icons.Filled.ThumbUp,
            contentDescription = "Like",
            tint = if (liked) FrTheme.accent else FrTheme.ink2,
            background = if (liked) FrTheme.accentSoft else Color.Transparent,
            onClick = onToggleLike,
        )
    }
}

@Composable
private fun SideButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: Color,
    background: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(52.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}

/** Display-only progress bar (no seeking). */
@Composable
private fun ProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val p = progress.coerceIn(0f, 1f)
    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(FrTheme.ink.copy(alpha = 0.14f)),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(p)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(FrTheme.accent),
        )
    }
}
