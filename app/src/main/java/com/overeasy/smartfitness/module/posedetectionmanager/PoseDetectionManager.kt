@file:OptIn(ExperimentalGetImage::class)

package com.overeasy.smartfitness.module.posedetectionmanager

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.println
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.Executors

class PoseDetectionManager(
    private val context: Context,
    private val cameraController: LifecycleCameraController
) {
    private var recording: Recording? = null
    private val cameraExecutor by lazy {
        ContextCompat.getMainExecutor(context)
    }
    private val poseDetectionExecutor by lazy {
        Executors.newSingleThreadExecutor()
    }
    private val options by lazy {
        AccuratePoseDetectorOptions.Builder()
            .setExecutor(poseDetectionExecutor)
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
    }

    private val poseDetector by lazy {
        PoseDetection.getClient(options)
    }

    @Composable
    fun PoseDetectionCameraX(
        modifier: Modifier = Modifier,
        onPoseDetected: (Pose) -> Unit
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current

        var cameraWidthPx by remember {
            mutableIntStateOf(0)
        }
        var cameraHeightPx by remember {
            mutableIntStateOf(0)
        }

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
                factory = {
                    PreviewView(it).apply {
                        controller = cameraController
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                }
            )
        }

        LaunchedEffect(Unit) {
            cameraController.videoCaptureQualitySelector = QualitySelector.from(Quality.SD)
            cameraController.setImageAnalysisAnalyzer(
                poseDetectionExecutor
            ) { imageProxy ->
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees

                val image = imageProxy.image

                if (image != null) {
                    val processImage = InputImage.fromMediaImage(image, rotationDegrees)

                    poseDetector.process(processImage)
                        .addOnSuccessListener { pose ->
                            if (pose.allPoseLandmarks.isNotEmpty()) {
                                onPoseDetected(pose)
                            }
                        }
                        .addOnFailureListener { e ->
                            println(
                                "jaehoLee",
                                "Exception is occur in PoseDetectionManager"
                            )
                            println("jaehoLee", "e: ${e.message}")
                            /* no-op */
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startRecording(
        isCameraPermissionGranted: Boolean,
        filesDir: File?
    ) {
        val dateString = LocalDateTime.now().run {
            "$year-" +
                    "${String.format("%02d", monthValue)}-" +
                    "${String.format("%02d", dayOfMonth)}_" +
                    "${String.format("%02d", hour)}:" +
                    "${String.format("%02d", minute)}:" +
                    "${String.format("%02d", second)}:" +
                    String.format("%02d", nano).take(5)
        }
        val outputFile = File(filesDir, "$dateString.mp4")
        if (!isCameraPermissionGranted) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        recording = cameraController.startRecording(
            FileOutputOptions.Builder(outputFile).build(),
            AudioConfig.create(false), // Added
            cameraExecutor,
        ) { event ->
            when(event) {
                is VideoRecordEvent.Start -> {
                    println("jaehoLee", "Video capture started")
                }
                is VideoRecordEvent.Finalize -> {
                    if(event.hasError()) {
                        recording?.close()
                        recording = null

                        println("jaehoLee", "Video capture failed")
                    } else {
                        MainApplication.appPreference.currentVideoFileDir = "$filesDir/$dateString.mp4"
                        println("jaehoLee", "Video capture succeeded")
                    }
                }
            }
        }
    }

    fun stopRecording() {
        recording?.stop()
        recording = null
    }
}