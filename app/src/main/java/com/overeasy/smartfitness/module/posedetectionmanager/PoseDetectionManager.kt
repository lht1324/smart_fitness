package com.overeasy.smartfitness.module.posedetectionmanager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.overeasy.smartfitness.println
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService


class PoseDetectionManager(
    private val cameraExecutor: ExecutorService,
    private val onPoseDetected: (Pose) -> Unit
) {
    private val options by lazy {
        AccuratePoseDetectorOptions.Builder()
            .setExecutor(cameraExecutor)
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .setPreferredHardwareConfigs(PoseDetectorOptions.CPU_GPU)
            .build()
    }

    private val poseDetector by lazy {
        PoseDetection.getClient(options)
    }

    @OptIn(ExperimentalGetImage::class)
    fun setImageAnalyzer(
        imageAnalysis: ImageAnalysis
    ) {
        imageAnalysis.setAnalyzer(
            cameraExecutor
        ) { imageProxy: ImageProxy ->
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
                        println("jaehoLee", "Exception is occur in PoseDetectionManager")
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