package com.jmoreira7.scrlcanvas.ui.vo

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.jmoreira7.scrlcanvas.data.model.OverlayItem

data class UiOverlayItem(
    val id: Int? = null,
    val imageUrl: String,
    val name: String,
    val position: Offset = Offset(0f, 0f),
    val size: Size? = null
)

fun OverlayItem.toUiOverlayItem(): UiOverlayItem {
    return UiOverlayItem(
        imageUrl = this.sourceUrl,
        name = this.overlayName
    )
}