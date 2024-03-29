package com.overeasy.smartfitness.module.videomanager

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object VideoManager {
    @Composable
    fun CameraX(
        modifier: Modifier = Modifier
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraScope = rememberCoroutineScope()
        val context = LocalContext.current
        val cameraX by remember { mutableStateOf<CameraX>(CameraXImpl()) }
        val previewView = remember { mutableStateOf<PreviewView?>(null) }
        val facing = cameraX.getFacingState().collectAsState()

        previewView.value?.let { preview ->
            AndroidView(
                modifier = modifier.fillMaxSize(),
                factory = { preview },
                update = { updatedPriviewView ->

                }
            )
        }

        LaunchedEffect(Unit) {
            cameraX.initialize(context = context)
            previewView.value = cameraX.getPreviewView()
        }
        DisposableEffect(facing.value) {
            cameraScope.launch(Dispatchers.Main) {
                cameraX.startCamera(lifecycleOwner = lifecycleOwner)
            }
            onDispose {
                cameraX.unBindCamera()
            }
        }
    }
}