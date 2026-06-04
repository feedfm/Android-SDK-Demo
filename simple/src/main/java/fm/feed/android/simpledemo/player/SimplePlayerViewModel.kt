package fm.feed.android.simpledemo.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import fm.feed.android.simpledemo.model.RadioStation
import fm.feed.android.playersdk.FeedAudioPlayer
import fm.feed.android.playersdk.FeedPlayerService
import fm.feed.android.playersdk.LikeStatusChangeListener
import fm.feed.android.playersdk.PlayListener
import fm.feed.android.playersdk.State
import fm.feed.android.playersdk.StateListener
import fm.feed.android.playersdk.StationChangedListener
import fm.feed.android.playersdk.error.FeedFMError
import fm.feed.android.playersdk.models.Play
import fm.feed.android.playersdk.models.Station

/**
 * Observable wrapper around [FeedPlayerService.getInstance]. Owns both the station library
 * and now-playing/UI state, and exposes intents to the UI.
 */
class SimplePlayerViewModel : ViewModel() {

    private val player: FeedAudioPlayer = FeedPlayerService.getInstance()

    // Library
    var stations by mutableStateOf<List<RadioStation>>(emptyList())
        private set

    // Now playing
    var activeStationId by mutableStateOf<Int?>(null)
        private set
    var title by mutableStateOf("")
        private set
    var artist by mutableStateOf("")
        private set
    var album by mutableStateOf("")
        private set
    var isPlaying by mutableStateOf(false)
        private set
    var canSkip by mutableStateOf(false)
        private set
    var liked by mutableStateOf(false)
        private set
    var disliked by mutableStateOf(false)
        private set
    var elapsed by mutableFloatStateOf(0f)
        private set
    var duration by mutableFloatStateOf(0f)
        private set

    /** Last player error message, or null. Set by the SDK's [PlayListener.onPlayerError]. */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // UI
    var isOpen by mutableStateOf(false)
    var isExpanded by mutableStateOf(false)

    val activeStation: RadioStation?
        get() = stations.firstOrNull { it.id == activeStationId }

    /** "Artist - Album" line for the players; omits whichever part is missing. */
    val artistAlbum: String
        get() = listOf(artist, album).filter { it.isNotBlank() }.joinToString(" - ")

    /** 0-based index of the active station, or null when none is active. */
    val activeStationIndex: Int?
        get() = stations.indexOfFirst { it.id == activeStationId }.takeIf { it >= 0 }

    /** Remaining seconds, or null when the current item has no known duration. */
    val remaining: Float?
        get() = if (duration > 0f) (duration - elapsed).coerceAtLeast(0f) else null

    val progress: Float
        get() = if (duration > 0f) (elapsed / duration).coerceIn(0f, 1f) else 0f

    private val stateListener = StateListener { state ->
        isPlaying = state == State.PLAYING
        if (state == State.READY_TO_PLAY) elapsed = 0f
    }

    private val playListener = object : PlayListener {
        override fun onSkipStatusChanged(status: Boolean) {
            canSkip = status
        }

        override fun onProgressUpdate(play: Play, elapsedTime: Float, duration: Float) {
            elapsed = elapsedTime
            this@SimplePlayerViewModel.duration = duration
        }

        override fun onPlayStarted(play: Play?) {
            val file = play?.audioFile
            title = file?.track?.title ?: ""
            artist = file?.artist?.name ?: ""
            album = file?.release?.title ?: ""
            duration = file?.durationInSeconds ?: 0f
            elapsed = 0f
            canSkip = player.canSkip()
            liked = file?.isLiked ?: false
            disliked = file?.isDisliked ?: false
        }

        override fun onPlayerError(error: FeedFMError) {
            // A play error doesn't necessarily stop playback — the SDK will try the next
            // song — but surface it so the failure isn't silent.
            errorMessage = error.message
        }
    }

    /** Clears the current [errorMessage] once the UI has shown it. */
    fun clearError() {
        errorMessage = null
    }

    private val stationChangedListener = StationChangedListener { station ->
        activeStationId = station.stableKey()
    }

    private val likeStatusChangeListener = LikeStatusChangeListener { audioFile ->
        liked = audioFile.isLiked
        disliked = audioFile.isDisliked
    }

    init {
        stations = player.stationList.mapIndexed { idx, s -> s.toRadioStation(idx) }
        activeStationId = player.activeStation?.stableKey()
        syncFromCurrentPlay()
        player.addStateListener(stateListener)
        player.addPlayListener(playListener)
        player.addStationChangedListener(stationChangedListener)
        player.addLikeStatusChangeListener(likeStatusChangeListener)
    }

    private fun syncFromCurrentPlay() {
        val file = player.currentPlay?.audioFile
        title = file?.track?.title ?: ""
        artist = file?.artist?.name ?: ""
        album = file?.release?.title ?: ""
        duration = file?.durationInSeconds ?: 0f
        elapsed = player.currentPlaybackTime
        isPlaying = player.state == State.PLAYING
        canSkip = player.canSkip()
        liked = file?.isLiked ?: false
        disliked = file?.isDisliked ?: false
    }

    // MARK: Intents

    fun select(station: RadioStation) {
        if (isOpen && activeStationId == station.id) {
            expand()
            return
        }
        val sdkStation = player.stationList.firstOrNull { it.stableKey() == station.id } ?: return
        player.play(sdkStation, withCrossfade = false)
        isOpen = true
        isExpanded = true
    }

    fun togglePlay() {
        if (isPlaying) player.pause() else player.play()
    }

    fun next() {
        player.skip(null)
    }

    fun toggleLike() {
        if (liked) player.unlike() else player.like()
    }

    fun toggleDislike() {
        if (disliked) player.unlike() else player.dislike()
    }

    fun expand() {
        isExpanded = true
    }

    fun minimize() {
        isExpanded = false
    }

    override fun onCleared() {
        super.onCleared()
        player.removeStateListener(stateListener)
        player.removePlayListener(playListener)
        player.removeStationChangedListener(stationChangedListener)
        player.removeLikeStatusChangeListener(likeStatusChangeListener)
    }
}

/**
 * Stable identity for an SDK [Station]. The SDK's `tempId` is the canonical id but is
 * nullable; fall back to the name hash so two distinct stations never collide on `0`.
 * Used consistently for [RadioStation.id], `activeStationId`, and the station lookup in
 * `select`.
 */
private fun Station.stableKey(): Int = tempId ?: name.hashCode()

/** Maps an SDK [Station] to the display-only [RadioStation], reading its options map. */
private fun Station.toRadioStation(index: Int): RadioStation =
    RadioStation.fromOptions(
        id = stableKey(),
        name = name,
        options = options ?: emptyMap(),
        index = index,
    )
