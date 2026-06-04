package fm.feed.android.simpledemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onMinimize: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAttribution by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(FrTheme.ink.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(onClick = onMinimize, modifier = Modifier.size(44.dp)) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Minimize",
                        tint = FrTheme.ink2,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.align(Alignment.Center),
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
            }
        }

        Spacer(Modifier.weight(1f))

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

        Spacer(Modifier.weight(1f))

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
            text = "Powered by Feed.fm",
            color = FrTheme.ink3,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable { showAttribution = true },
        )

        Spacer(Modifier.height(24.dp))
    }

    if (showAttribution) {
        AttributionSheet(onDismiss = { showAttribution = false })
    }
}

/**
 * Bottom sheet shown when "Powered by Feed.fm" is tapped. Dims the player behind a
 * scrim; dismissed by tapping outside the sheet or dragging it down.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttributionSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = FrTheme.surface1,
        dragHandle = { BottomSheetDefaults.DragHandle(color = FrTheme.ink3) },
    ) {
        Text(
            text = "There is no affiliation, connection, association or endorsement of the " +
                "products, goods or services displayed on this page by the copyright owners, " +
                "featured recording artists and authors of the sound recordings (and the " +
                "musical works embodied therein) transmitted through the Feed.fm player",
            color = FrTheme.ink2,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(bottom = 32.dp),
        )
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
