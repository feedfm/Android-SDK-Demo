package fm.feed.android.simpledemo.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WaveformTest {
    @Test
    fun returnsRequestedCount() {
        assertEquals(7, Waveform.barHeights(seed = 1, bars = 7).size)
        assertEquals(11, Waveform.barHeights(seed = 3, bars = 11).size)
    }

    @Test
    fun zeroBarsReturnsEmpty() {
        assertTrue(Waveform.barHeights(seed = 1, bars = 0).isEmpty())
    }

    @Test
    fun negativeBarsReturnsEmpty() {
        assertTrue(Waveform.barHeights(seed = 1, bars = -3).isEmpty())
    }

    @Test
    fun heightsWithinRange() {
        for (h in Waveform.barHeights(seed = 5, bars = 20)) {
            assertTrue("height $h >= 0.32", h >= 0.32)
            assertTrue("height $h <= 0.94", h <= 0.94)
        }
    }

    @Test
    fun deterministicForSameSeed() {
        assertEquals(
            Waveform.barHeights(seed = 2, bars = 9),
            Waveform.barHeights(seed = 2, bars = 9)
        )
    }

    @Test
    fun differsBySeed() {
        assertNotEquals(
            Waveform.barHeights(seed = 1, bars = 9),
            Waveform.barHeights(seed = 2, bars = 9)
        )
    }
}
