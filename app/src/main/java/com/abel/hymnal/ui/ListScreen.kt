package com.abel.hymnal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abel.hymnal.data.Hymn
import com.abel.hymnal.data.Lang
import com.abel.hymnal.ui.theme.Tokens

@Composable
fun ListScreen(vm: HymnalViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Tokens.Bg)
    ) {
        Header(
            lang = vm.lang,
            query = vm.query,
            onLangChange = vm::switchLanguage,
            onQueryChange = vm::onQueryChange
        )

        val hymns = vm.filteredHymns
        when {
            vm.loading -> EmptyState("Loading hymns…")

            vm.allHymns.isEmpty() -> EmptyState(
                "No hymn data found.\nCheck that hymns_cn.json and hymns_en.json exist in app/src/main/assets."
            )

            hymns.isEmpty() -> EmptyState("No hymns match your search.")

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hymns, key = { it.id }) { hymn ->
                    HymnCard(hymn) { vm.openHymn(hymn.id) }
                }
            }
        }
    }
}

@Composable
private fun Header(
    lang: Lang,
    query: String,
    onLangChange: (Lang) -> Unit,
    onQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Tokens.Bg)
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "赞美诗 ",
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Tokens.TextPrimary
                )
                Text(
                    text = "·",
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Tokens.Accent
                )
                Text(
                    text = " Hymnal",
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Tokens.TextPrimary
                )
            }
            LangToggle(lang, onLangChange)
        }

        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Tokens.Muted)
            },
            placeholder = {
                Text(
                    if (lang == Lang.CN) "搜索编号或歌名…" else "Search number or title…",
                    color = Tokens.Muted,
                    fontSize = 15.sp
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Tokens.Surface,
                unfocusedContainerColor = Tokens.Surface,
                focusedBorderColor = Tokens.Accent,
                unfocusedBorderColor = Tokens.Border,
                focusedTextColor = Tokens.TextPrimary,
                unfocusedTextColor = Tokens.TextPrimary,
                cursorColor = Tokens.Accent
            )
        )
    }

    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Tokens.Border)
    )
}

@Composable
private fun LangToggle(lang: Lang, onLangChange: (Lang) -> Unit) {
    Row(
        modifier = Modifier
            .background(Tokens.Surface, CircleShape)
            .border(1.dp, Tokens.Border, CircleShape)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        LangButton("中文", lang == Lang.CN) { onLangChange(Lang.CN) }
        LangButton("EN", lang == Lang.EN) { onLangChange(Lang.EN) }
    }
}

@Composable
private fun LangButton(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (active) Tokens.Accent else Color.Transparent, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) Color.White else Tokens.Muted
        )
    }
}

@Composable
private fun HymnCard(hymn: Hymn, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 480.dp)
            .background(Tokens.Surface, RoundedCornerShape(18.dp))
            .border(1.dp, Tokens.Border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = hymn.id.toString().padStart(3, '0'),
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Tokens.Accent,
            modifier = Modifier.width(32.dp)
        )
        Text(
            text = hymn.title,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Tokens.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Tokens.BorderHi,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(top = 60.dp),
            color = Tokens.Muted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}
