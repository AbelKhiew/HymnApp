# 赞美诗 Hymnal — Android (Kotlin + Jetpack Compose)

A native Android port of the HTML/JS hymnal app. Same dark theme, same sliding
detail view, same verse navigation, font-size steps, and inline pinyin toggle.

## Open in Android Studio

1. Unzip, then **File → Open** and select the `HymnalApp` folder.
2. Let Gradle sync (it downloads AGP 8.5.2, Kotlin 1.9.24, Compose BOM 2024.06).
   If Android Studio offers to upgrade AGP/Kotlin, accepting is fine.
3. Run on a device or emulator with API 26+.

If sync complains about a missing Gradle wrapper JAR, choose
**Use Gradle from: 'Specified location' / wrapper** in
*Settings → Build Tools → Gradle*, or run `gradle wrapper` once — Android Studio
usually handles this automatically.

## Where the hymn data lives

The original `Hymnsc.js` / `Hymne.js` files were converted verbatim to JSON:

```
app/src/main/assets/hymns_cn.json   (124 hymns)
app/src/main/assets/hymns_en.json   (2 hymns)
```

The schema is unchanged — `id`, `title`, `sections[].verse`, `sections[].content[].cn`,
`.py`, `.type` — so `HymnRepository` reads exactly the same fields the web app did,
and lookup is still by hymn `id`.

To add hymns, edit the JSON directly, or re-export from the JS files with Node:

```js
const fs = require('fs');
const hymns = new Function(fs.readFileSync('Hymnsc.js', 'utf8') + '\nreturn hymns_cn;')();
fs.writeFileSync('hymns_cn.json', JSON.stringify(hymns.sort((a, b) => a.id - b.id)));
```

## Project layout

| File | Purpose |
|---|---|
| `MainActivity.kt` | Hosts Compose, swaps list ⇄ detail with a slide animation, handles back |
| `ui/HymnalViewModel.kt` | Language, search query, verse index, font step, pinyin flag; persists prefs in SharedPreferences (the `localStorage` equivalent) |
| `ui/ListScreen.kt` | Header, CN/EN toggle, search box, hymn cards |
| `ui/DetailScreen.kt` | Verse card, progress dots, A−/A+, pinyin toggle, PREV/NEXT, swipe left/right |
| `ui/Pinyin.kt` | Port of `buildInlineHTML()` — pairs each Chinese character with its pinyin syllable |
| `ui/theme/Theme.kt` | The CSS `:root` color tokens |
| `data/` | Serializable models + asset loader |

## Behaviour notes

- Search filters on title or number, same as `filterHymns()`. LazyColumn makes the
  120-item list smooth, so no debounce is needed.
- Font steps are the same 1.0 → 3.0 rem ladder, applied as `sp` (1 rem = 16 sp).
- Keyboard arrows in the web app become a horizontal swipe on the lyrics area.
- A section is treated as a chorus if the section or any of its lines has
  `type: "chorus"` — the CN data marks it on the line.
