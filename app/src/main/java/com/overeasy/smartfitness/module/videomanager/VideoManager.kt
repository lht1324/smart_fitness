package com.overeasy.smartfitness.module.videomanager

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.pxToDp
import com.overeasy.smartfitness.ui.theme.ColorLightGreen
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.ColorSunday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object VideoManager {
    @Composable
    fun PoseDetectionCameraX(
        modifier: Modifier = Modifier,
        onPoseDetected: (Pose) -> Unit
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraScope = rememberCoroutineScope()
        val context = LocalContext.current

        var cameraWidthPx by remember {
            mutableIntStateOf(0)
        }
        var cameraHeightPx by remember {
            mutableIntStateOf(0)
        }

        val cameraX by remember {
            mutableStateOf<CameraX>(
                PoseDetectionCameraXImpl(
                    cameraWidth = cameraWidthPx,
                    cameraHeight = cameraHeightPx,
                    onPoseDetected = onPoseDetected
                )
            )
        }
        val previewView = remember { mutableStateOf<PreviewView?>(null) }
        val facing = cameraX.getFacingState().collectAsState()

        previewView.value?.let { preview ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AndroidView(
                    modifier = modifier
                        .fillMaxSize()
                        .onSizeChanged { (width, height) ->
                            if (width != cameraWidthPx) {
                                cameraWidthPx = width
                            }
                            if (height != cameraHeightPx) {
                                cameraHeightPx = height
                            }
                        },
                    factory = { preview },
                    update = { updatedPriviewView ->

                    }
                )
            }
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