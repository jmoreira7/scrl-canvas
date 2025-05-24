package com.jmoreira7.scrlcanvas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SCRLCanvasTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = lightColorScheme(
        background = Color.DarkGray
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}