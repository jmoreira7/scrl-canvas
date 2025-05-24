package com.jmoreira7.scrlcanvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmoreira7.scrlcanvas.data.AppostropheApiManager
import com.jmoreira7.scrlcanvas.ui.vo.UiOverlayCategory
import com.jmoreira7.scrlcanvas.ui.vo.toUiOverlayCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val showSheet: Boolean = false,
    val overlays: List<UiOverlayCategory> = emptyList()
)

class MainViewModel(
    private val apiManager: AppostropheApiManager
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            apiManager.fetchOverlays().collect { categories ->
                _state.update { currentState ->
                    currentState.copy(
                        overlays = categories.map { it.toUiOverlayCategory() }
                    )
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