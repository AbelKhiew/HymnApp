package com.abel.hymnal.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Loads hymn data from assets/hymns_cn.json and assets/hymns_en.json.
 * These files are a direct conversion of the original Hymnsc.js / Hymne.js data,
 * with the same fields (id, title, sections, content, cn, py, type).
 */
object HymnRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val cache = mutableMapOf<Lang, List<Hymn>>()

    suspend fun load(context: Context, lang: Lang): List<Hymn> = withContext(Dispatchers.IO) {
        cache[lang]?.let { return@withContext it }

        val fileName = if (lang == Lang.CN) "hymns_cn.json" else "hymns_en.json"
        val hymns = runCatching {
            val text = context.assets.open(fileName).bufferedReader().use { it.readText() }
            json.decodeFromString<List<Hymn>>(text).sortedBy { it.id }
        }.getOrDefault(emptyList())

        cache[lang] = hymns
        hymns
    }

    /** Same lookup semantics as the web app: find by hymn id. */
    fun findById(hymns: List<Hymn>, id: Int): Hymn? = hymns.firstOrNull { it.id == id }
}
