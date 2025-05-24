package com.jmoreira7.scrlcanvas.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private const val PICTURE_PX = 1080
private const val CANVAS_WIDTH_IN_PICTURES = 3

@Composable
fun ScrollableCanvas() {
    val density = LocalDensity.current.density
    val canvasHeight = (PICTURE_PX / density).dp
    val canvasWidth = ((PICTURE_PX * CANVAS_WIDTH_IN_PICTURES) / density).dp

    Box(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .height(canvasHeight)
                .width(canvasWidth)
        ) {
            drawRect(color = Color.White)
            for (i in 1 until CANVAS_WIDTH_IN_PICTURES) {
                val x = (i * (PICTURE_PX)).toFloat()
                drawLine(
                    color = Color.Black,
                    start = Offset(x = x, y = 0f),
                    end = Offset(x = x, y = size.height),
                    strokeWidth = 4f
                )
            }
        }
    }
}