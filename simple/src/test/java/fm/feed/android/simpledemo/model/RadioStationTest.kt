package fm.feed.android.simpledemo.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RadioStationTest {
    @Test
    fun parsesNameAndId() {
        val s = RadioStation.fromOptions(id = 7, name = "Deep Focus", options = emptyMap(), index = 0)
        assertEquals(7, s.id)
        assertEquals("Deep Focus", s.name)
    }

    @Test
    fun parsesSubheaderWhenPresent() {
        val s = RadioStation.fromOptions(
            id = 1, name = "X",
            options = mapOf("subheader" to "Minimal beats for flow"), index = 0
        )
        assertEquals("Minimal beats for flow", s.subheader)
    }

    @Test
    fun subheaderNullWhenMissingOrEmpty() {
        assertNull(RadioStation.fromOptions(1, "X", emptyMap(), 0).subheader)
        assertNull(RadioStation.fromOptions(1, "X", mapOf("subheader" to ""), 0).subheader)
    }

    @Test
    fun parsesBackgroundImageUrlWhenValid() {
        val s = RadioStation.fromOptions(
            id = 1, name = "X",
            options = mapOf("background_image_url" to "https://example.com/a.jpg"), index = 0
        )
        assertEquals("https://example.com/a.jpg", s.backgroundImageUrl)
    }

    @Test
    fun backgroundImageUrlNullWhenMissingOrEmpty() {
        assertNull(RadioStation.fromOptions(1, "X", emptyMap(), 0).backgroundImageUrl)
        assertNull(RadioStation.fromOptions(1, "X", mapOf("background_image_url" to ""), 0).backgroundImageUrl)
    }

    @Test
    fun gradientIsDeterministicByIndex() {
        val palette = RadioStation.gradientPalette
        assertEquals(palette[0], RadioStation.fromOptions(1, "X", emptyMap(), 0).gradient)
        assertEquals(palette[1], RadioStation.fromOptions(1, "X", emptyMap(), 1).gradient)
    }

    @Test
    fun gradientWrapsAroundPalette() {
        val palette = RadioStation.gradientPalette
        val wrapped = RadioStation.fromOptions(1, "X", emptyMap(), palette.size).gradient
        assertEquals(palette[0], wrapped)
    }
}
