package com.jmoreira7.scrlcanvas.data

import com.jmoreira7.scrlcanvas.data.model.OverlayCategory
import kotlinx.coroutines.flow.Flow

interface OverlayRepository {
    suspend fun fetchOverlays(): Flow<List<OverlayCategory>>
}