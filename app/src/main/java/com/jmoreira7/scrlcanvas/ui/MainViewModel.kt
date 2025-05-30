package com.jmoreira7.scrlcanvas.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmoreira7.scrlcanvas.data.OverlayRepositoryImpl
import com.jmoreira7.scrlcanvas.ui.vo.UiOverlayCategory
import com.jmoreira7.scrlcanvas.ui.vo.UiOverlayItem
import com.jmoreira7.scrlcanvas.ui.vo.toUiOverlayCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class UiState(
    val showSheet: Boolean = false,
    val overlays: List<UiOverlayCategory> = emptyList(),
    val canvasOverlays: List<UiOverlayItem> = emptyList(),
    val selectedOverlayId: Int? = null
)

class MainViewModel(
    private val overlayRepository: OverlayRepositoryImpl
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            overlayRepository.fetchOverlays().collect { categories ->
                val mappedOverlays = withContext(Dispatchers.Default) {
                    categories.map { it.toUiOverlayCategory() }
                }

                _state.update { currentState ->
                    currentState.copy(overlays = mappedOverlays)
                }
            }
        }
    }

    fun openSheet() {
        _state.update { currentState ->
            currentState.copy(showSheet = true)
        }
    }

    fun closeSheet() {
        _state.update { currentState ->
            currentState.copy(showSheet = false)
        }
    }

    fun addOverlayToCanvas(overlay: UiOverlayItem): Int {
        val newOverlayId = UUID.randomUUID().hashCode()

        _state.update { currentState ->
            currentState.copy(
                canvasOverlays = currentState.canvasOverlays + overlay.copy(
                    id = newOverlayId
                )
            )
        }

        return newOverlayId
    }

    fun selectOverlay(overlayId: Int?) {
        _state.update { currentState ->
            currentState.copy(selectedOverlayId = overlayId)
        }
    }

    fun moveOverlay(overlayId: Int?, newPosition: Offset) {
        _state.update { currentState ->
            currentState.copy(
                canvasOverlays = currentState.canvasOverlays.map { overlay ->
                    if (overlay.id == overlayId) overlay.copy(position = newPosition) else overlay
                }
            )
        }
    }

    fun setOverlaySize(overlayId: Int, size: Size) {
        _state.update { currentState ->
            currentState.copy(
                canvasOverlays = currentState.canvasOverlays.map { overlay ->
                    if (overlay.id == overlayId) overlay.copy(size = size) else overlay
                }
            )
        }
    }
}