package com.jmoreira7.scrlcanvas.data

import com.jmoreira7.scrlcanvas.data.model.OverlayCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class OverlayRepositoryImpl(
    private val apiManager: ApiManager
) : OverlayRepository {
    override suspend fun fetchOverlays(): Flow<List<OverlayCategory>> {
        return apiManager.fetchOverlays().flowOn(Dispatchers.IO)
    }
}