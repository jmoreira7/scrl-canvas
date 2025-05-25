package com.jmoreira7.scrlcanvas.ui.utils

data class SnapResult(
    var minDist: Float,
    var snap: Pair<Float, Int>? = null, // Pair(snapTo, movingOverlayPointsIndex)
    var snapDelta: Float = 0f
)