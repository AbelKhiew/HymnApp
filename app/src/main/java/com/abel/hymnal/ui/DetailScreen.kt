package com.abel.hymnal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abel.hymnal.data.Hymn
import com.abel.hymnal.data.Line
import com.abel.hymnal.ui.theme.Tokens
import kotlin.math.abs

@Composable
fun DetailScreen(vm: HymnalViewModel, hymn: Hymn) {
    val total = hymn.sections.size
    val index = vm.verseIndex.coerceIn(0, (total - 1).coerceAtLeast(0))
    val section = hymn.sections.getOrNull(index)
    val scrollState = rememberScrollState()

    LaunchedEffect(index, hymn.id) { scrollState.scrollTo(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Tokens.Bg)
    ) {
        // ── Header ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Tokens.Bg)
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(Color.Transparent, RoundedCornerShape(10.dp))
                    .clickable { vm.closeHymn() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Back to list",
                    tint = Tokens.Accent,
                    modifier = Modifier.size(22.dp)
                )
                Text("Back", color = Tokens.Accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Text(
                text = hymn.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 60.dp),
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Tokens.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Tokens.Border)
        )

        // ── Lyrics (scrollable, swipeable) ──────────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .pointerInput(hymn.id, index) {
                    var dragTotal = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { dragTotal = 0f },
                        onDragEnd = {
                            if (abs(dragTotal) > 80f) vm.changeVerse(if (dragTotal < 0) 1 else -1)
                        }
                    ) { _, amount -> dragTotal += amount }
                }
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            VerseCard(
                lines = section?.content.orEmpty(),
                inlineMode = vm.inlineMode,
                lyricSp = vm.lyricFontSp
            )
        }

        // ── Controls ────────────────────────────────────────────
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Tokens.Border)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Tokens.Bg)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionBadge(
                text = "VERSE ${index + 1} / $total"
            )

            ProgressDots(total = total, active = index, onJump = vm::jumpToVerse)

            ControlRow(
                fontLabel = "${FONT_STEPS[vm.fontStepIndex]}r",
                inlineOn = vm.inlineMode,
                prevEnabled = index > 0,
                nextEnabled = index < total - 1,
                onFontChange = vm::changeFontSize,
                onToggleInline = vm::toggleInlineMode,
                onPrev = { vm.changeVerse(-1) },
                onNext = { vm.changeVerse(1) }
            )
        }
    }
}

@Composable
private fun VerseCard(lines: List<Line>, inlineMode: Boolean, lyricSp: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(Tokens.CardTop, Tokens.CardBottom)),
                RoundedCornerShape(24.dp)
            )
            .border(1.dp, Tokens.Border, RoundedCornerShape(24.dp))
            .padding(horizontal = 24.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        lines.forEach { line ->
            if (inlineMode) InlineLyricLine(line, lyricSp) else BlockLyricLine(line, lyricSp)
        }
    }
}

@Composable
private fun BlockLyricLine(line: Line, lyricSp: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = line.cn,
            fontFamily = FontFamily.Serif,
            fontSize = lyricSp.sp,
            lineHeight = (lyricSp * 1.35f).sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        if (!line.py.isNullOrBlank()) {
            Spacer(Modifier.height(5.dp))
            Text(
                text = line.py,
                fontSize = (lyricSp * 0.55f).sp,
                lineHeight = (lyricSp * 0.8f).sp,
                fontStyle = FontStyle.Italic,
                color = Tokens.Muted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InlineLyricLine(line: Line, lyricSp: Float) {
    val groups = remember(line.cn, line.py) { buildCharGroups(line.cn, line.py) }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        groups.forEach { group ->
            Column(
                modifier = Modifier.padding(horizontal = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = group.py.ifEmpty { " " },
                    fontSize = (lyricSp * 0.48f).sp,
                    lineHeight = (lyricSp * 0.67f).sp,
                    fontStyle = FontStyle.Italic,
                    color = Tokens.Muted
                )
                Text(
                    text = group.cn,
                    fontFamily = FontFamily.Serif,
                    fontSize = lyricSp.sp,
                    lineHeight = (lyricSp * 1.2f).sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (group.dimmed) Tokens.Muted else Color.White
                )
            }
        }
    }
}

@Composable
private fun SectionBadge(text: String) {
    Box(
        modifier = Modifier
            .background(Tokens.Surface, CircleShape)
            .border(1.dp, Tokens.Border, CircleShape)
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Tokens.Accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProgressDots(total: Int, active: Int, onJump: (Int) -> Unit) {
    FlowRow(
        modifier = Modifier.width(260.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(if (i == active) 10.dp else 7.dp)
                    .background(if (i == active) Tokens.Accent else Tokens.BorderHi, CircleShape)
                    .clickable { onJump(i) }
            )
        }
    }
}

@Composable
private fun ControlRow(
    fontLabel: String,
    inlineOn: Boolean,
    prevEnabled: Boolean,
    nextEnabled: Boolean,
    onFontChange: (Int) -> Unit,
    onToggleInline: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CtrlButton("A−") { onFontChange(-1) }
        CtrlButton("A+") { onFontChange(1) }

        Divider()

        CtrlButton(text = "字pīn", active = inlineOn, onClick = onToggleInline)

        Divider()

        NavButton("← PREV", prevEnabled, Modifier.weight(1f), onPrev)
        NavButton("NEXT →", nextEnabled, Modifier.weight(1f), onNext)
    }
}

@Composable
private fun CtrlButton(text: String, active: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = 44.dp)
            .background(
                if (active) Tokens.AccentDim else Tokens.Surface,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (active) Tokens.Accent else Tokens.Border,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (active) Tokens.AccentText else Tokens.TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun Divider() {
    Box(
        Modifier
            .width(1.dp)
            .height(24.dp)
            .background(Tokens.Border)
    )
}

@Composable
private fun NavButton(text: String, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .heightIn(min = 44.dp)
            .alpha(if (enabled) 1f else 0.2f)
            .background(Tokens.AccentDim, RoundedCornerShape(12.dp))
            .border(1.dp, Tokens.Accent, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Tokens.AccentText,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.6.sp
        )
    }
}
