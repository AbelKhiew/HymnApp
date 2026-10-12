package com.abel.hymnal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abel.hymnal.ui.DetailScreen
import com.abel.hymnal.ui.HymnalViewModel
import com.abel.hymnal.ui.ListScreen
import com.abel.hymnal.ui.theme.HymnalTheme
import com.abel.hymnal.ui.theme.Tokens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            HymnalTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Tokens.Bg) {
                    HymnalApp()
                }
            }
        }
    }
}

@Composable
fun HymnalApp(vm: HymnalViewModel = viewModel()) {
    val hymn = vm.openHymn

    // Keep the last opened hymn around so the slide-out animation has content to draw.
    var lastHymn by remember { mutableStateOf(hymn) }
    if (hymn != null) lastHymn = hymn

    BackHandler(enabled = hymn != null) { vm.closeHymn() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Tokens.Bg)
    ) {
        ListScreen(vm)

        // Detail slides in from the right, like the web app's #detailView panel.
        AnimatedVisibility(
            visible = hymn != null,
            enter = slideInHorizontally(animationSpec = tween(350)) { it },
            exit = slideOutHorizontally(animationSpec = tween(350)) { it }
        ) {
            lastHymn?.let { shown ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Tokens.Bg)
                ) {
                    DetailScreen(vm, shown)
                }
            }
        }
    }
}
