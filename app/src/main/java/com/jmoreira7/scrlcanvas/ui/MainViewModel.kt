package com.jmoreira7.scrlcanvas.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UiState(
    val showSheet: Boolean = false,
)

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

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