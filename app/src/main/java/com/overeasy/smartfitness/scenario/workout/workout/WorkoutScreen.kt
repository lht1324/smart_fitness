@file:OptIn(ExperimentalLayoutApi::class, ExperimentalPermissionsApi::class)

package com.overeasy.smartfitness.scenario.workout.workout

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.module.posedetectionmanager.PoseDetectionManager
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.pxToDp
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.public.OutlinedText
import com.overeasy.smartfitness.ui.theme.ColorLightGreen
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.ColorSunday
import com.overeasy.smartfitness.ui.theme.fontFamily
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@Composable
fun WorkoutScreen(
    modifier: Modifier = Modifier,
    viewModel: WorkoutViewModel = hiltViewModel(),
    filesDir: File?,
    onClickWatchExampleVideo: (String) -> Unit,
    onFinishWorkout: (Int) -> Unit,
    onChangeIsWorkoutRunning: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE or
                        CameraController.IMAGE_ANALYSIS
            )
//            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }
    val poseDetectionManager = remember {
        PoseDetectionManager(
            context = context,
            cameraController = cameraController
        )
    }
    var isCameraPermissionGranted by remember { mutableStateOf(false) }
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA,
        onPermissionResult = { isGranted ->
            isCameraPermissionGranted = isGranted
            MainApplication.appPreference.isAlreadyRequestedCameraPermission = true
        }
    )

    val isImeVisible = WindowInsets.isImeVisible

    var isShowWorkoutInfoInputDialog by remember { mutableStateOf(false) }
    var isShowWorkoutWarningDialog by remember { mutableStateOf(false) }
    var isShowFinishWorkoutDialog by remember { mutableStateOf(false) }

    var cameraWidthPx by remember { mutableIntStateOf(0) }
    var cameraHeightPx by remember { mutableIntStateOf(0) }

    var isRecording by remember { mutableStateOf(false) }
    var isSaveEnabled by remember { mutableStateOf(false) }

    val isWorkoutInfoInitialized by viewModel.isWorkoutInfoInitialized.collectAsState(false)

    val workoutDataList = remember { viewModel.workoutDataList }

    val bodyFrameData by viewModel.bodyFrameData.collectAsState()

    val currentSet by viewModel.currentSet.collectAsState()

    var temporaryWorkoutInfo by remember { mutableStateOf<WorkoutInfo?>(null) }

    val firstCountdownTimer by viewModel.firstCountdown.collectAsState()
    val restCountdownTimer by viewModel.restCountdown.collectAsState()
    val restTime by viewModel.restTime.collectAsState(initial = 30)

    val scorePerfect by viewModel.scorePerfect.collectAsState()
    val scoreGood by viewModel.scoreGood.collectAsState()
    val scoreNotGood by viewModel.scoreNotGood.collectAsState()

    val isWorkoutRunning by viewModel.isWorkoutRunning.collectAsState()
    val isLoadingFinishWorkout by viewModel.isLoadingFinishWorkout.collectAsState()

    val uploadLoadingProgress by viewModel.uploadLoadingProgress.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        poseDetectionManager.PoseDetectionCameraX(
            modifier = Modifier.onSizeChanged { (width, height) ->
                if (width != cameraWidthPx) {
                    cameraWidthPx = width
                }

                if (height != cameraHeightPx) {
                    cameraHeightPx = height
                }
            },
            onPoseDetected = { pose ->
                viewModel.onUpdatePose(
                    cameraWidthPx = cameraWidthPx,
                    cameraHeightPx = cameraHeightPx,
                    pose = pose
                )
            }
        )

        bodyFrameData?.run {
            if (offsetList.size == 26) { // 선 갯수
                BodyFrame(
                    modifier = Modifier.size(
                        width = context.pxToDp(cameraWidthPx).dp,
                        height = context.pxToDp(cameraHeightPx).dp
                    ),
                    bodyFrameData = this
                )
            }
        }
        if (isWorkoutRunning) {
            ScoreBoard(
                modifier = Modifier.padding(top = 20.dp, end = 20.dp),
                currentSet = currentSet,
                totalPerfect = scorePerfect,
                totalGood = scoreGood,
                totalNotGood = scoreNotGood
            )
        }
        RecordingButton(
            modifier = Modifier
                .padding(bottom = 20.dp),
            onClickButton = {
                if (isCameraPermissionGranted) {
                    if (!isRecording) {
                        if (isWorkoutInfoInitialized) {
                            viewModel.onClickRecordButtonWhenWorkoutInfoAlreadyExists()
                        } else {
                            isShowWorkoutInfoInputDialog = true
                        }
                    } else {
                        isShowFinishWorkoutDialog = true
                    }
                }
            },
            isRecording = isRecording
        )
        if (firstCountdownTimer != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.5f))
                    .noRippleClickable { /* no-op */ }
            ) {
                Text(
                    text = firstCountdownTimer.toString(),
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                    fontSize = 48.dpToSp(),
                    fontWeight = FontWeight.Black,
                    fontFamily = fontFamily
                )
            }
        }
        if (restCountdownTimer != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.5f))
                    .noRippleClickable { /* no-op */ }
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 48.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = restCountdownTimer.toString(),
                        color = Color.White,
                        fontSize = 48.dpToSp(),
                        fontWeight = FontWeight.Black,
                        fontFamily = fontFamily
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val loadingProgress by remember {
                            derivedStateOf {
                                val currentProgress = (restTime - (restCountdownTimer ?: 0)).toFloat() / restTime.toFloat()

                                1.0f * currentProgress
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(color = Color.Black)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(loadingProgress)
                                .height(20.dp)
                                .background(color = Color.White)
                                .align(Alignment.CenterStart)
                        )
                    }
                }
            }
        }
        if (isLoadingFinishWorkout) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = ColorPrimary)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(100.dp),
                        color = ColorSaturday,
                        strokeWidth = 15.dp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "AI가 운동 결과를 열심히 분석 중이에요.\n잠시만 기다려주세요...",
                        color = Color.White,
                        fontSize = 24.dpToSp(),
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Center
                    )
                    if (uploadLoadingProgress != null) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp)
                                    .background(color = Color.Black)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = uploadLoadingProgress!!)
                                    .height(20.dp)
                                    .background(color = Color.White)
                                    .align(Alignment.CenterStart)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${String.format("%.2f", uploadLoadingProgress!! * 100f)}%",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = Color.White,
                            fontSize = 20.dpToSp(),
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = fontFamily
                        )
                    }
                }
            }
        }
    }

    if (isShowWorkoutInfoInputDialog) {
        AlertDialog(
            onDismissRequest = {
                isShowWorkoutInfoInputDialog = false
            },
            confirmButton = @Composable {

            },
            modifier = Modifier.heightIn(max = (screenHeight.toFloat() * 0.6f).dp),
            title = {
                Text(
                    text = "운동 정보 입력하기",
                    color = Color.Black,
                    fontSize = 24.dpToSp(),
                    fontWeight = FontWeight.Black,
                    fontFamily = fontFamily
                )
            },
            text = {
                WorkoutInfoInputDialog(
                    workoutDataList = workoutDataList,
                    isImeVisible = isImeVisible,
                    onClickWatchExampleVideo = onClickWatchExampleVideo,
                    onFinish = { workoutInfo ->
//                        restTime = workoutInfo.restTime ?: 30
//                        viewModel.setWorkoutInfo(workoutInfo)
                        temporaryWorkoutInfo = workoutInfo
                        isShowWorkoutWarningDialog = true
//                        isShowWorkoutInfoInputDialog = false
                    }
                )
            },
            titleContentColor = Color.Black,
            containerColor = Color.White
        )
    }

    if (isShowWorkoutWarningDialog) {
        Dialog(
            title = "운동 전 확인해주세요!",
            customDescription = {
                Column(
                    modifier = Modifier
                        .verticalScroll(state = rememberScrollState())
                ) {
                    Text(
                        text = "⚠\uFE0F 운동 중 앱을 나갔다 들어오면 운동이 종료돼요!",
                        color = Color.Black,
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fontFamily
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.5.dp,
                        color = Color(0xFF919191)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "1. 화면에 대각선으로 보이도록 자세를 잡아주세요.\n" +
                                "\n" +
                                "2. 화면에 몸을 최대한 꽉 채워주세요.\n" +
                                "\n" +
                                "3. 몸을 적당히 내린다면 운동이 감지되지 않을 수도 있어요.",
                        color = Color.Black,
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.Normal,
                        fontFamily = fontFamily
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.5.dp,
                        color = Color(0xFF919191)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "'확인'을 누르면 운동이 시작돼요!",
                        color = Color.Black,
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fontFamily
                    )
                }
            },
            confirmText = "취소",
            dismissText = "확인",
            onClickConfirm = {
                temporaryWorkoutInfo = null
                isShowWorkoutWarningDialog = false
            },
            onClickDismiss = {
                temporaryWorkoutInfo?.run {
                    viewModel.setWorkoutInfo(this)
                }
                isShowWorkoutWarningDialog = false
                isShowWorkoutInfoInputDialog = false
            },
            onDismissRequest = {
                isShowWorkoutWarningDialog = false
            }
        )
    }

    if (isShowFinishWorkoutDialog) {
        Dialog(
            title = "운동을 종료하시겠어요?",
            description = "지금까지 한 운동 정보가 저장되지 않아요.",
            confirmText = "아니",
            dismissText = "응",
            onClickConfirm = {
                isShowFinishWorkoutDialog = false
            },
            onClickDismiss = {
                viewModel.onClickStopWorkout()
                isShowFinishWorkoutDialog = false
            },
            onDismissRequest = {
                isShowFinishWorkoutDialog = false
            }
        )
    }

    BackHandler(enabled = isWorkoutRunning || firstCountdownTimer != null) {
        isShowFinishWorkoutDialog = true
    }

    LaunchedEffect(isWorkoutRunning, firstCountdownTimer) {
        onChangeIsWorkoutRunning(isWorkoutRunning || firstCountdownTimer != null)
    }

    LaunchedEffect(cameraPermissionState.status.isGranted) {
        isCameraPermissionGranted = cameraPermissionState.status.isGranted
    }

    LaunchedEffect(cameraController.isRecording) {
        isRecording = cameraController.isRecording
        viewModel.setIsRecording(cameraController.isRecording)
    }

    LaunchedEffect(viewModel.workoutUiEvent) {
        viewModel.workoutUiEvent.collectLatest { event ->
            when (event) {
                WorkoutViewModel.WorkoutUiEvent.StartRecording -> {
                    if (cameraController.initializationFuture.isDone) {
                        poseDetectionManager.startRecording(
                            isCameraPermissionGranted = true,
                            filesDir = filesDir
                        )
                    }
                }
                WorkoutViewModel.WorkoutUiEvent.StopRecording -> {
                    poseDetectionManager.stopRecording()
                }
                WorkoutViewModel.WorkoutUiEvent.StartFakeRecording -> {
                    isRecording = true
                }
                WorkoutViewModel.WorkoutUiEvent.StopFakeRecording -> {
                    isRecording = false
                }
                is WorkoutViewModel.WorkoutUiEvent.FinishWorkout -> {
                    onFinishWorkout(event.noteId)
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (isWorkoutRunning)
                        viewModel.onClickStopWorkout()
                }
                else -> { /* no-op */ }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun BoxScope.ScoreBoard(
    modifier: Modifier = Modifier,
    currentSet: Int,
    totalPerfect: Int,
    totalGood: Int,
    totalNotGood: Int
) {
    Column(
        modifier = modifier
            .background(color = Color.White.copy(alpha = 0.3f))
            .align(Alignment.TopEnd)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${currentSet}세트",
                color = Color.Black,
                fontSize = 24.dpToSp(),
                fontWeight = FontWeight.Black,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedText(
                text = "Perfect: $totalPerfect",
                textColor = ColorSaturday
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedText(
                text = "Good: $totalGood",
                textColor = ColorLightGreen
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedText(
                text = "Not Good: $totalNotGood",
                textColor = ColorSunday
            )
        }
    }
}

@Composable
private fun BoxScope.RecordingButton(
    modifier: Modifier = Modifier,
    onClickButton: () -> Unit,
    isRecording: Boolean
) {
    Box(
        modifier = modifier
            .size(70.dp)
            .noRippleClickable(onClick = onClickButton)
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = CircleShape
            )
            .align(Alignment.BottomCenter)
    ) {
        if (!isRecording) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = Color.Red,
                        shape = CircleShape
                    )
                    .align(Alignment.Center)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(color = Color.Black)
                    .align(Alignment.Center)
            )
        }
    }
}