package fm.feed.android.simpledemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import fm.feed.android.playersdk.AvailabilityListener
import fm.feed.android.playersdk.FeedAudioPlayer
import fm.feed.android.playersdk.FeedPlayerService
import fm.feed.android.simpledemo.player.SimplePlayerViewModel
import fm.feed.android.simpledemo.ui.FullPlayerSheet
import fm.feed.android.simpledemo.ui.MiniPlayerBar
import fm.feed.android.simpledemo.ui.StationListScreen
import fm.feed.android.simpledemo.ui.theme.FrTheme
import fm.feed.android.simpledemo.ui.theme.SimpleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FrTheme.screenBG),
                ) {
                    NotificationPermissionRequest()
                    AvailabilityGate()
                }
            }
        }
    }
}

/**
 * Requests POST_NOTIFICATIONS once on Android 13+ so the media-playback notification and
 * lock-screen controls posted by FeedPlayerService are visible. On older versions the
 * permission doesn't exist and this is a no-op. Playback is unaffected if the user declines;
 * only the notification is suppressed.
 */
@Composable
private fun NotificationPermissionRequest() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* granted or not, playback continues either way */ }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

private enum class Availability { LOADING, AVAILABLE, UNAVAILABLE }

@Composable
private fun AvailabilityGate() {
    var status by remember { mutableStateOf(Availability.LOADING) }

    DisposableEffect(Unit) {
        // getInstance(listener) is safe to call even after availability has already been
        // determined: the SDK invokes the listener immediately in that case. So this still
        // resolves correctly when re-run after a configuration change.
        FeedPlayerService.getInstance(object : AvailabilityListener {
            override fun onPlayerAvailable(player: FeedAudioPlayer) {
                status = Availability.AVAILABLE
            }

            override fun onPlayerUnavailable(e: Exception) {
                status = Availability.UNAVAILABLE
            }
        })
        onDispose { }
    }

    when (status) {
        Availability.LOADING -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator(color = FrTheme.accent)
        }
        Availability.UNAVAILABLE -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Player is not available", color = FrTheme.ink2)
        }
        Availability.AVAILABLE -> RootScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RootScreen(vm: SimplePlayerViewModel = viewModel()) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val active = vm.activeStation
    val seed = (vm.activeStationIndex ?: 0) + 1
    val snackbarHostState = remember { SnackbarHostState() }

    // Surface SDK playback errors transiently, then clear them.
    LaunchedEffect(vm.errorMessage) {
        vm.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            vm.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(FrTheme.screenBG)) {
        StationListScreen(
            stations = vm.stations,
            activeStationId = vm.activeStationId,
            isPlayerOpen = vm.isOpen,
            isPlaying = vm.isPlaying,
            onStationClick = vm::select,
            modifier = Modifier.fillMaxSize(),
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        if (vm.isOpen && active != null) {
            MiniPlayerBar(
                station = active,
                seed = seed,
                title = vm.title,
                artist = vm.artistAlbum,
                isPlaying = vm.isPlaying,
                progress = vm.progress,
                onTap = vm::expand,
                onTogglePlay = vm::togglePlay,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
            )
        }
    }

    if (vm.isExpanded && active != null) {
        ModalBottomSheet(
            onDismissRequest = vm::minimize,
            sheetState = sheetState,
            containerColor = FrTheme.screenBG,
            // Cover the entire display: square corners, no grab handle, no inset reserved
            // for the status bar. The sheet content applies its own safe-area padding.
            shape = RectangleShape,
            dragHandle = null,
            windowInsets = WindowInsets(0, 0, 0, 0),
        ) {
            FullPlayerSheet(
                station = active,
                seed = seed,
                title = vm.title,
                artist = vm.artistAlbum,
                isPlaying = vm.isPlaying,
                canSkip = vm.canSkip,
                liked = vm.liked,
                disliked = vm.disliked,
                elapsed = vm.elapsed,
                remaining = vm.remaining,
                progress = vm.progress,
                onTogglePlay = vm::togglePlay,
                onSkip = vm::next,
                onToggleLike = vm::toggleLike,
                onToggleDislike = vm::toggleDislike,
                onMinimize = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { vm.minimize() }
                },
            )
        }
    }
}
