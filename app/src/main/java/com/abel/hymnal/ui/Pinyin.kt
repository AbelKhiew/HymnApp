package com.abel.hymnal.ui

/** One rendered unit in inline mode: a pinyin syllable stacked over its character(s). */
data class CharGroup(val py: String, val cn: String, val dimmed: Boolean)

private val LEADING_MARKS =
    Regex("^[①②③④⑤⑥⑦⑧⑨⑩\\[【副歌\\]】()（）]+\\s*")

private val TOKEN = Regex(
    "[\\u4e00-\\u9fff][，。；：？！、\u201C\u201D\u2018\u2019「」『』（）…—～·]?|[^\\u4e00-\\u9fff]+"
)

private val HAN = Regex("[\\u4e00-\\u9fff]")

/**
 * Port of buildInlineHTML() from the web app: pairs each Chinese character with the
 * next pinyin syllable, leaving punctuation and latin text unpaired (dimmed).
 */
fun buildCharGroups(cn: String, py: String?): List<CharGroup> {
    if (py.isNullOrBlank()) return listOf(CharGroup("", cn, false))

    val pyParts = py.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    val cleaned = LEADING_MARKS.replace(cn, "")
    val tokens = TOKEN.findAll(cleaned).map { it.value }.toList()
        .ifEmpty { listOf(cleaned) }

    val groups = mutableListOf<CharGroup>()
    var pyIndex = 0

    for (token in tokens) {
        val isChinese = HAN.containsMatchIn(token)
        if (isChinese && pyIndex < pyParts.size) {
            groups += CharGroup(pyParts[pyIndex++], token, false)
        } else {
            // Split latin/punctuation runs on spaces so long runs can wrap.
            token.split(" ").filter { it.isNotBlank() }.forEach { piece ->
                groups += CharGroup("", piece, true)
            }
        }
    }
    return groups
}
