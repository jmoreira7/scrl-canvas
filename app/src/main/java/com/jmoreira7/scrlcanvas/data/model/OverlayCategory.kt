package com.jmoreira7.scrlcanvas.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OverlayCategory(
    val title: String,
    val id: Int,
    val items: List<OverlayItem>,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String,
)
