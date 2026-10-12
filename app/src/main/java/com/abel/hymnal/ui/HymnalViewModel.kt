package com.abel.hymnal.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abel.hymnal.data.Hymn
import com.abel.hymnal.data.HymnRepository
import com.abel.hymnal.data.Lang
import kotlinx.coroutines.launch

val FONT_STEPS = listOf(1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.25f, 2.5f, 2.75f, 3.0f)
const val BASE_LYRIC_SP = 16f   // 1rem == 16sp, same scale as the web app

class HymnalViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("hymnal_prefs", Context.MODE_PRIVATE)

    var lang by mutableStateOf(
        runCatching { Lang.valueOf(prefs.getString("pref_lang", "CN") ?: "CN") }.getOrDefault(Lang.CN)
    )
        private set

    var query by mutableStateOf("")
        private set

    var allHymns by mutableStateOf<List<Hymn>>(emptyList())
        private set

    var loading by mutableStateOf(true)
        private set

    var fontStepIndex by mutableIntStateOf(prefs.getInt("pref_font_step", 3))
        private set

    var inlineMode by mutableStateOf(prefs.getBoolean("pref_inline", false))
        private set

    /** Currently open hymn id, or null when the list is showing. */
    var openHymnId by mutableStateOf<Int?>(null)
        private set

    var verseIndex by mutableIntStateOf(0)
        private set

    init {
        loadData(lang)
    }

    val filteredHymns: List<Hymn>
        get() {
            val q = query.trim().lowercase()
            if (q.isEmpty()) return allHymns
            return allHymns.filter {
                it.title.lowercase().contains(q) || it.id.toString().contains(q)
            }
        }

    val openHymn: Hymn?
        get() = openHymnId?.let { id -> HymnRepository.findById(allHymns, id) }

    val lyricFontSp: Float
        get() = FONT_STEPS[fontStepIndex] * BASE_LYRIC_SP

    private fun loadData(target: Lang) {
        loading = true
        viewModelScope.launch {
            allHymns = HymnRepository.load(getApplication(), target)
            loading = false
        }
    }

    fun switchLanguage(target: Lang) {
        if (lang == target) return
        lang = target
        prefs.edit().putString("pref_lang", target.name).apply()
        query = ""
        openHymnId = null
        loadData(target)
    }

    fun onQueryChange(value: String) {
        query = value
    }

    fun openHymn(id: Int) {
        openHymnId = id
        verseIndex = 0
    }

    fun closeHymn() {
        openHymnId = null
    }

    fun changeVerse(delta: Int) {
        val total = openHymn?.sections?.size ?: return
        val next = verseIndex + delta
        if (next in 0 until total) verseIndex = next
    }

    fun jumpToVerse(index: Int) {
        val total = openHymn?.sections?.size ?: return
        if (index in 0 until total) verseIndex = index
    }

    fun changeFontSize(direction: Int) {
        fontStepIndex = (fontStepIndex + direction).coerceIn(0, FONT_STEPS.lastIndex)
        prefs.edit().putInt("pref_font_step", fontStepIndex).apply()
    }

    fun toggleInlineMode() {
        inlineMode = !inlineMode
        prefs.edit().putBoolean("pref_inline", inlineMode).apply()
    }
}
