package com.jmoreira7.scrlcanvas.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jmoreira7.scrlcanvas.ui.theme.Woodsmoke50
import com.jmoreira7.scrlcanvas.ui.theme.overlaySelection
import com.jmoreira7.scrlcanvas.ui.vo.UiOverlayItem
import kotlin.math.roundToInt

private const val PICTURE_PX = 1080
private const val CANVAS_WIDTH_IN_PICTURES = 3
private const val OVERLAY_MAX_SIZE = 150

@Composable
fun ScrollableCanvas(
    overlays: List<UiOverlayItem> = emptyList(),
    selectedOverlayId: Int? = null,
    onSelectOverlay: (Int?) -> Unit = {},
    onMoveOverlay: (Int, Offset) -> Unit = { _, _ -> }
) {
    val density = LocalDensity.current
    val logicalDensity = LocalDensity.current.density
    val canvasHeightDp = (PICTURE_PX / logicalDensity).dp
    val canvasHeightPx = with(density) { canvasHeightDp.toPx() }
    val canvasWidthDp = ((PICTURE_PX * CANVAS_WIDTH_IN_PICTURES) / logicalDensity).dp
    val canvasWidthPx = with(density) { canvasWidthDp.toPx() }

    Box(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .height(canvasHeightDp)
                .width(canvasWidthDp)
        ) {
            drawRect(color = Color.White)
            for (i in 1 until CANVAS_WIDTH_IN_PICTURES) {
                val x = (i * (PICTURE_PX)).toFloat()
                drawLine(
                    color = Woodsmoke50,
                    start = Offset(x = x, y = 0f),
                    end = Offset(x = x, y = size.height),
                    strokeWidth = 4f
                )
            }
        }

        if (overlays.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onSelectOverlay(null) }
            ) {
                overlays.forEach { overlay ->
                    val isSelected = overlay.id == selectedOverlayId
                    val dragOffset = remember { mutableStateOf(overlay.position) }
                    val overlaySizePx = remember { mutableStateOf(Size(0f, 0f)) }

                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    overlay.position.x.roundToInt(),
                                    overlay.position.y.roundToInt()
                                )
                            }
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) { onSelectOverlay(overlay.id) }
                            .pointerInput(overlay.id) {
                                detectDragGestures(
                                    onDragStart = { onSelectOverlay(overlay.id) },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        if (isSelected) {
                                            dragOffset.value = getClampedToCanvasOffset(
                                                newOffset = dragOffset.value + dragAmount,
                                                overlaySizePx = overlaySizePx.value,
                                                canvasHeightPx = canvasHeightPx,
                                                canvasWidthPx = canvasWidthPx
                                            )
                                            onMoveOverlay(overlay.id, dragOffset.value)
                                        }
                                    },
                                )
                            }
                    ) {
                        AsyncImage(
                            model = overlay.imageUrl,
                            contentDescription = overlay.name,
                            modifier = Modifier
                                .sizeIn(
                                    maxWidth = OVERLAY_MAX_SIZE.dp,
                                    minWidth = OVERLAY_MAX_SIZE.dp
                                )
                                .onGloballyPositioned { coordinates ->
                                    overlaySizePx.value = Size(
                                        coordinates.size.width.toFloat(),
                                        coordinates.size.height.toFloat()
                                    )
                                }
                        )

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(MaterialTheme.colorScheme.overlaySelection)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getClampedToCanvasOffset(
    newOffset: Offset,
    overlaySizePx: Size,
    canvasHeightPx: Float,
    canvasWidthPx: Float
): Offset {
    val clampedX = newOffset.x.coerceIn(0f, canvasWidthPx - overlaySizePx.width)
    val clampedY = newOffset.y.coerceIn(0f, canvasHeightPx - overlaySizePx.height)
    return Offset(clampedX, clampedY)
}