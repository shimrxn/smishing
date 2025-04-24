package com.example.smishingdetectionapp

import androidx.compose.ui.platform.ComposeView

fun injectPulsing(view: ComposeView) {
    view.setContent {
        Pulsing()
    }
}
