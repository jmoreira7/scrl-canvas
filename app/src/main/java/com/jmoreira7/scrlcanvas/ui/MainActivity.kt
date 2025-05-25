package com.jmoreira7.scrlcanvas.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jmoreira7.scrlcanvas.ui.components.AddButton
import com.jmoreira7.scrlcanvas.ui.components.OverlaysSheet
import com.jmoreira7.scrlcanvas.ui.components.ScrollableCanvas
import com.jmoreira7.scrlcanvas.ui.theme.SCRLCanvasTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SCRLCanvasTheme {
                val uiState by viewModel.state.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    ScrollableCanvas(
                        overlays = uiState.canvasOverlays,
                        selectedOverlayId = uiState.selectedOverlayId,
                        onSelectOverlay = { id -> viewModel.selectOverlay(id) },
                        onMoveOverlay = { id, pos -> viewModel.moveOverlay(id, pos) }
                    )
                    AddButton(onClick = { viewModel.openSheet() })

                    if (uiState.showSheet) {
                        OverlaysSheet(
                            overlays = uiState.overlays,
                            onDismissRequest = { viewModel.closeSheet() },
                            onOverlayClick = { overlay ->
                                val newOverlayId = viewModel.addOverlayToCanvas(overlay)
                                viewModel.selectOverlay(newOverlayId)
                                viewModel.closeSheet()
                            }
                        )
                    }
                }
            }
        }
    }
}