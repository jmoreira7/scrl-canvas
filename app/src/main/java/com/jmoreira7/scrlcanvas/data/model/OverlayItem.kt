package com.jmoreira7.scrlcanvas.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OverlayItem(
    val id: Int,
    @SerialName("overlay_name")
    val overlayName: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("category_id")
    val categoryId: Int,
    @SerialName("source_url")
    val sourceUrl: String,
    @SerialName("is_premium")
    val isPremium: Boolean,
    @SerialName("includes_scrl_branding")
    val includesScrlBranding: Boolean,
    @SerialName("premium_uses_last_48hrs")
    val premiumUsesLast48Hrs: Int,
    @SerialName("max_canvas_size")
    val maxCanvasSize: Int
)
