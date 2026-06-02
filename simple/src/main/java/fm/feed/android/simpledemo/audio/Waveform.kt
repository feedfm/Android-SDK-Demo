package fm.feed.android.simpledemo.audio

/**
 * Deterministic bar-graph "waveform" heights for fallback station artwork.
 * Seeds are expected to be non-negative (station indices).
 */
object Waveform {
    /** Returns [bars] heights in 0.32..0.94, deterministic for a given [seed]. */
    fun barHeights(seed: Int, bars: Int): List<Double> {
        if (bars <= 0) return emptyList()
        var x: Long = (seed.toLong() + 1L) * 9301L + 49297L
        val out = ArrayList<Double>(bars)
        repeat(bars) {
            x = (x * 9301L + 49297L) % 233280L
            out.add(0.32 + (x.toDouble() / 233280.0) * 0.62)
        }
        return out
    }
}
