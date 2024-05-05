package com.overeasy.smartfitness.module.videomanager

import android.content.Context
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.overeasy.smartfitness.model.workout.RecordingInfo
import com.overeasy.smartfitness.model.workout.RecordingState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ExecutorService

interface CameraX {
    fun initialize(context: Context)
    fun startCamera(lifecycleOwner: LifecycleOwner)
    fun takePicture(showMessage: (String) -> Unit)
    fun startRecordVideo()
    fun stopRecordVideo()
    fun resumeRecordVideo()
    fun pauseRecordVideo()
    fun closeRecordVideo()
    fun flipCameraFacing()
    fun turnOnOffFlash()
    fun unBindCamera()
    fun getPreviewView() : PreviewView
    fun getFlashState() : StateFlow<Boolean>
    fun getFacingState() : StateFlow<Int>
    fun getRecordingState() : StateFlow<RecordingState>
    fun getRecordingInfo() : SharedFlow<RecordingInfo>

    fun getProvider(): ProcessCameraProvider
    fun getExecutor(): ExecutorService
    fun getPreview(): Preview
}