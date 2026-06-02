package fm.feed.android.simpledemo.audio

import kotlin.math.roundToInt

/** Formats playback times for the player UI. */
object TimeFormat {
    /**
     * Formats seconds as `m:ss` (e.g. `0:01`, `3:33`).
     * Non-finite or non-positive input formats as `0:00`.
     */
    fun mmss(seconds: Double): String {
        if (!seconds.isFinite() || seconds <= 0.0) return "0:00"
        val total = seconds.roundToInt()
        return "${total / 60}:" + (total % 60).toString().padStart(2, '0')
    }

    /** [Float] convenience overload (the SDK reports playback time as [Float]). */
    fun mmss(seconds: Float): String = mmss(seconds.toDouble())
}
