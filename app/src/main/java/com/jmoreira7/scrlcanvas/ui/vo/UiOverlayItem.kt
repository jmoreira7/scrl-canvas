package com.jmoreira7.scrlcanvas.ui.vo

import com.jmoreira7.scrlcanvas.data.model.OverlayItem

data class UiOverlayItem(
    val imageUrl: String
)

fun OverlayItem.toUiOverlayItem(): UiOverlayItem {
    return UiOverlayItem(
        imageUrl = this.sourceUrl
    )
}