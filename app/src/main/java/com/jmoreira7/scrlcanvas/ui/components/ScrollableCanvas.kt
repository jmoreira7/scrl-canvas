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
import androidx.compose.ui.geometry.Rect
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
import com.jmoreira7.scrlcanvas.ui.utils.SnapResult
import com.jmoreira7.scrlcanvas.ui.vo.UiOverlayItem
import kotlin.math.abs
import kotlin.math.roundToInt

private const val PICTURE_PX = 1080
private const val CANVAS_WIDTH_IN_PICTURES = 3
private const val OVERLAY_MAX_SIZE = 100
private const val SNAP_THRESHOLD = 10f
private const val SNAP_PROXIMITY = 20f

@Composable
fun ScrollableCanvas(
    overlays: List<UiOverlayItem> = emptyList(),
    selectedOverlayId: Int? = null,
    onSelectOverlay: (Int?) -> Unit = {},
    onMoveOverlay: (Int?, Offset) -> Unit = { _, _ -> }
) {
    val density = LocalDensity.current
    val logicalDensity = LocalDensity.current.density
    val canvasHeightDp = (PICTURE_PX / logicalDensity).dp
    val canvasHeightPx = with(density) { canvasHeightDp.toPx() }
    val canvasWidthDp = ((PICTURE_PX * CANVAS_WIDTH_IN_PICTURES) / logicalDensity).dp
    val canvasWidthPx = with(density) { canvasWidthDp.toPx() }

    val snapLines = remember { mutableStateOf<List<Pair<Offset, Offset>>>(emptyList()) }

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
                                            val proposedOffset = dragOffset.value + dragAmount
                                            val (snappedOffset, lines) = getSnappedOffsetAndLines(
                                                movingOverlayId = overlay.id,
                                                proposedOffset = proposedOffset,
                                                overlaySizePx = overlaySizePx.value,
                                                overlays = overlays
                                            )

                                            dragOffset.value = getClampedToCanvasOffset(
                                                newOffset = snappedOffset,
                                                overlaySizePx = overlaySizePx.value,
                                                canvasHeightPx = canvasHeightPx,
                                                canvasWidthPx = canvasWidthPx
                                            )
                                            snapLines.value = lines
                                            onMoveOverlay(overlay.id, dragOffset.value)
                                        }
                                    },
                                    onDragEnd = { snapLines.value = emptyList() }
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

                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                ) {
                    snapLines.value.forEach { (start, end) ->
                        drawLine(
                            color = Color.Yellow,
                            start = start,
                            end = end,
                            strokeWidth = 4f
                        )
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

private fun getSnappedOffsetAndLines(
    movingOverlayId: Int?,
    proposedOffset: Offset,
    overlaySizePx: Size,
    overlays: List<UiOverlayItem>
): Pair<Offset, List<Pair<Offset, Offset>>> {
    var snappedOffset = proposedOffset
    val lines = mutableListOf<Pair<Offset, Offset>>()
    val movingOverlayRect = Rect(proposedOffset, overlaySizePx)
    val snapResultX = SnapResult(minDist = SNAP_THRESHOLD + 1)
    val snapResultY = SnapResult(minDist = SNAP_THRESHOLD + 1)

    overlays.filter { overlay -> overlay.id != movingOverlayId }.forEach { otherOverlay ->
        val otherOverlayRect = Rect(otherOverlay.position, overlaySizePx)
        val isWithinYSnapRange = movingOverlayRect.bottom + SNAP_PROXIMITY > otherOverlayRect.top &&
                movingOverlayRect.top - SNAP_PROXIMITY < otherOverlayRect.bottom
        val isWithinXSnapRange = movingOverlayRect.right + SNAP_PROXIMITY > otherOverlayRect.left &&
                movingOverlayRect.left - SNAP_PROXIMITY < otherOverlayRect.right

        if (isWithinYSnapRange) {
            finClosestSnap(
                movingOverlayAxisPoints = listOf(
                    movingOverlayRect.left, movingOverlayRect.right, movingOverlayRect.center.x
                ),
                otherOverlayAxisPoints = listOf(
                    otherOverlayRect.left, otherOverlayRect.right, otherOverlayRect.center.x
                ),
                snapResult = snapResultX
            )
        }

        if (isWithinXSnapRange) {
            finClosestSnap(
                movingOverlayAxisPoints = listOf(
                    movingOverlayRect.top, movingOverlayRect.bottom, movingOverlayRect.center.y
                ),
                otherOverlayAxisPoints = listOf(
                    otherOverlayRect.top, otherOverlayRect.bottom, otherOverlayRect.center.y
                ),
                snapResult = snapResultY
            )
        }
    }

    snappedOffset = snappedOffset.copy(x = snappedOffset.x + snapResultX.snapDelta)
    snapResultX.snap?.let { snap ->
        lines.add(Offset(snap.first, 0f) to Offset(snap.first, Float.MAX_VALUE))
    }

    snappedOffset = snappedOffset.copy(y = snappedOffset.y + snapResultY.snapDelta)
    snapResultY.snap?.let { snap ->
        lines.add(Offset(0f, snap.first) to Offset(Float.MAX_VALUE, snap.first))
    }

    return snappedOffset to lines
}

private fun finClosestSnap(
    movingOverlayAxisPoints: List<Float>,
    otherOverlayAxisPoints: List<Float>,
    snapResult: SnapResult
) {
    movingOverlayAxisPoints.forEachIndexed { index, movingX ->
        otherOverlayAxisPoints.forEach { otherX ->
            val dist = abs(movingX - otherX)
            snapResult.apply {
                if (dist < minDist && dist < SNAP_THRESHOLD) {
                    minDist = dist
                    snap = otherX to index
                    snapDelta = otherX - movingX
                } else if (dist == minDist && dist < SNAP_THRESHOLD) {
                    snap?.let {
                        if (index == 2 && it.second != 2) {
                            snap = otherX to index
                            snapDelta = otherX - movingX
                        }
                    }
                }
            }
        }
    }
}