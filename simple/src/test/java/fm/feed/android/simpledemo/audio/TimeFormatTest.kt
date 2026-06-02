package fm.feed.android.simpledemo.audio

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatTest {
    @Test
    fun formatsWholeMinutes() {
        assertEquals("0:00", TimeFormat.mmss(0.0))
        assertEquals("1:00", TimeFormat.mmss(60.0))
        assertEquals("10:00", TimeFormat.mmss(600.0))
    }

    @Test
    fun formatsSecondsWithLeadingZero() {
        assertEquals("0:01", TimeFormat.mmss(1.0))
        assertEquals("0:09", TimeFormat.mmss(9.0))
        assertEquals("3:33", TimeFormat.mmss(213.0))
    }

    @Test
    fun roundsToNearestSecond() {
        assertEquals("0:01", TimeFormat.mmss(1.4))
        assertEquals("0:02", TimeFormat.mmss(1.6))
    }

    @Test
    fun clampsInvalidInput() {
        assertEquals("0:00", TimeFormat.mmss(-5.0))
        assertEquals("0:00", TimeFormat.mmss(Double.NaN))
        assertEquals("0:00", TimeFormat.mmss(Double.POSITIVE_INFINITY))
    }

    @Test
    fun floatOverloadMatchesDouble() {
        assertEquals("3:33", TimeFormat.mmss(213f))
        assertEquals("0:00", TimeFormat.mmss(-1f))
    }
}
