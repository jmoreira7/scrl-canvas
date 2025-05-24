package com.jmoreira7.scrlcanvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmoreira7.scrlcanvas.data.OverlayRepositoryImpl
import com.jmoreira7.scrlcanvas.ui.vo.UiOverlayCategory
import com.jmoreira7.scrlcanvas.ui.vo.toUiOverlayCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UiState(
    val showSheet: Boolean = false,
    val overlays: List<UiOverlayCategory> = emptyList()
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
}