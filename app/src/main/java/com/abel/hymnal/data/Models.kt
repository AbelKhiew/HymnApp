package com.abel.hymnal.data

import kotlinx.serialization.Serializable

/**
 * Mirrors the original JS shape:
 *   { id, title, sections: [ { verse, type?, content: [ { cn, py?, type? } ] } ] }
 */
@Serializable
data class Hymn(
    val id: Int,
    val title: String = "",
    val sections: List<Section> = emptyList()
)

@Serializable
data class Section(
    val verse: Int? = null,
    val type: String? = null,
    val content: List<Line> = emptyList()
) {
    /** A section counts as a chorus if it says so, or if any of its lines do. */
    val isChorus: Boolean
        get() = type?.equals("chorus", true) == true ||
                content.any { it.type?.equals("chorus", true) == true }
}

@Serializable
data class Line(
    val cn: String = "",
    val py: String? = null,
    val type: String? = null
)

enum class Lang { CN, EN }
