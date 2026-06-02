package fm.feed.android.simpledemo.model

/**
 * A station prepared for display, decoupled from the SDK's [fm.feed.android.playersdk.models.Station]
 * so it can be constructed and tested from a plain options map.
 *
 * Build instances with [fromOptions], which derives the display fields from a station's raw
 * options map.
 *
 * @param id stable identity for the station, used for active-station matching. Typically the SDK
 *   station's `tempId`, but callers may substitute a fallback when `tempId` is null.
 * @param subheader optional secondary line, from the `subheader` option.
 * @param backgroundImageUrl optional artwork URL, from the `background_image_url` option.
 * @param gradient fallback artwork colors, picked deterministically from [gradientPalette].
 */
data class RadioStation(
    val id: Int,
    val name: String,
    val subheader: String?,
    val backgroundImageUrl: String?,
    val gradient: List<String>,
) {
    companion object {
        /**
         * Brand-derived gradient pairs, used for fallback artwork when a station has no
         * `background_image_url`.
         */
        val gradientPalette: List<List<String>> = listOf(
            listOf("#61B978", "#007680"),
            listOf("#02B2AF", "#001E2A"),
            listOf("#9747FF", "#001E2A"),
            listOf("#26A69A", "#004D40"),
            listOf("#FB7460", "#5A23B3"),
            listOf("#FA5E49", "#9747FF"),
            listOf("#6FC084", "#2C763F"),
            listOf("#007680", "#0F3A56"),
        )

        /**
         * Builds a [RadioStation] from a station's id, name, raw options map, and list index.
         *
         * @param index position in the station list, used to pick a deterministic [gradient].
         */
        fun fromOptions(
            id: Int,
            name: String,
            options: Map<String, Any?>,
            index: Int,
        ): RadioStation {
            val subheader = (options["subheader"] as? String)?.takeIf { it.isNotEmpty() }
            val backgroundImageUrl = (options["background_image_url"] as? String)?.takeIf { it.isNotEmpty() }
            val count = gradientPalette.size
            val gradient = gradientPalette[((index % count) + count) % count]
            return RadioStation(id, name, subheader, backgroundImageUrl, gradient)
        }
    }
}
