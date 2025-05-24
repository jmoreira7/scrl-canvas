package com.jmoreira7.scrlcanvas.ui.vo

import com.jmoreira7.scrlcanvas.data.model.OverlayCategory

data class UiOverlayCategory(
    val name: String,
    val items: List<UiOverlayItem>
)

fun OverlayCategory.toUiOverlayCategory(): UiOverlayCategory {
    return UiOverlayCategory(
        name = this.title,
        items = this.items.map { it.toUiOverlayItem() }
    )
}