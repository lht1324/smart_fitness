@file:OptIn(ExperimentalCoroutinesApi::class)

package com.overeasy.smartfitness.scenario.workout.workout

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.calculateAngle
import com.overeasy.smartfitness.domain.ai.AiRepository
import com.overeasy.smartfitness.domain.ai.model.LandmarkInfo
import com.overeasy.smartfitness.domain.exercises.ExercisesRepository
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.entity.PostWorkoutDataReq
import com.overeasy.smartfitness.domain.workout.model.workout.WorkoutData
import com.overeasy.smartfitness.getNormalizedFrameFloatArray
import com.overeasy.smartfitness.model.workout.BodyFrameData
import com.overeasy.smartfitness.module.tensorflowmanager.TensorFlowManager
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.toLandmarkInfo
import com.overeasy.smartfitness.toPair
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timer


@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val aiRepository: AiRepository,
    private val exercisesRepository: ExercisesRepository,
    private val tensorFlowManager: TensorFlowManager
) : ViewModel() {
    private val _workoutUiEvent = MutableSharedFlow<WorkoutUiEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val workoutUiEvent = _workoutUiEvent.asSharedFlow()

//    private val _workoutNameList = mutableStateListOf("푸쉬업", "데드리프트", "딥스", "스쿼트", "숄더프레스")
    private val _workoutDataList = mutableStateListOf<Pair<String, Boolean>>()
    val workoutDataList = _workoutDataList

    private val _bodyFrameData = MutableStateFlow<BodyFrameData?>(null)
    val bodyFrameData = _bodyFrameData.asStateFlow()

    private val noteId = MutableStateFlow(-1)
    private val workoutInfo = MutableStateFlow<WorkoutInfo?>(null)
    private val workoutName = workoutInfo.map { info ->
        info?.workoutName
    }
    private val workoutNameForVideo = MutableStateFlow("")
    private val _restTime = workoutInfo.filter { info ->
        info?.restTime != null
    }.map { info ->
        info?.restTime!!
    }
    val restTime = _restTime
    val isWorkoutInfoInitialized = workoutInfo.map { info ->
        info != null
    }

    private val setDataList = workoutInfo.map { info ->
        info?.setDataList?.filter { setData ->
            setData.run { weight != null && repeats != null }
        }
    }
    private val setAmount = setDataList.map { list ->
        list?.size
    }

    private val _currentSet = MutableStateFlow(0)
    val currentSet = _currentSet.asStateFlow()
    private val currentSetWeight = setDataList.flatMapLatest { list ->
        currentSet.filter { set ->
            set > 0
        }.map { set ->
            list?.get(set - 1)?.weight
        }
    }
    private val currentSetCount = setDataList.flatMapLatest { list ->
        currentSet.filter { set ->
            set > 0
        }.map { set ->
            list?.get(set - 1)?.repeats
        }
    }

    private val poseList = mutableStateListOf<Pose>()

    private val updatedPose = MutableSharedFlow<Pose>()

    private val _scorePerfect = MutableStateFlow(0)
    val scorePerfect = _scorePerfect.asStateFlow()
    private val _scoreGood = MutableStateFlow(0)
    val scoreGood = _scoreGood.asStateFlow()
    private val _scoreNotGood = MutableStateFlow(0)
    val scoreNotGood = _scoreNotGood.asStateFlow()

    private val totalCount = combine(scorePerfect, scoreGood, scoreNotGood) { perfect, good, notGood ->
        perfect + good + notGood
    }

    private val workoutDataReq = combine(noteId, workoutInfo) { noteId, workoutInfo ->
        noteId to workoutInfo
    }.filter { (_, workoutInfo) ->
        workoutInfo != null
    }.map { (noteId, workoutInfo) ->
        noteId to workoutInfo!!
    }.map { (noteId, workoutInfo) ->
        PostWorkoutDataReq(
            noteId = noteId,
            exerciseName = workoutInfo.workoutName,
            workoutList = workoutInfo.setDataList.filter { setData ->
                setData.run { weight != null && repeats != null }
            }.mapIndexed { index, setData ->
                setData.run {
                    WorkoutData(
                        setNum = index + 1,
                        repeats = repeats,
                        weight = weight,
                        scorePerfect = scorePerfect,
                        scoreGood = scoreGood,
                        scoreBad = scoreBad,
                    )
                }
            }
        )
    }

    private val _firstCountdown = MutableStateFlow<Int?>(null)
    val firstCountdown = _firstCountdown.asStateFlow()
    private val _restCountdown = MutableStateFlow<Int?>(null)
    val restCountdown = _restCountdown.asStateFlow()

    private val currentWorkoutTime = MutableStateFlow(0L)
    private var firstTimer: Timer? = null
    private var restTimer: Timer? = null
    private var workoutTimer: Timer? = null

    private val _isWorkoutRunning = MutableStateFlow(false)
    val isWorkoutRunning = _isWorkoutRunning.asStateFlow()

    private val _isLoadingFinishWorkout = MutableStateFlow(false)
    val isLoadingFinishWorkout = _isLoadingFinishWorkout.asStateFlow()

    private val isReachedFirstHighestPointPushUp = MutableStateFlow(false)
    private val isReachedLastHighestPointPushUp = MutableStateFlow(false)
    private val isReachedLowestPointPushUp = MutableStateFlow(false)

    private val landmarkInfoList = MutableStateFlow<List<LandmarkInfo>>(listOf())

    private val interpreter = workoutName.distinctUntilChanged().map { name ->
        when (name) {
            "푸쉬업" -> "pushup_model.tflite"
            "데드리프트" -> "deadlift_model.tflite"
            "스쿼트" -> "squat_model.tflite"
            else -> null
        }
    }.filterNotNull().map { modelPath ->
        tensorFlowManager.getInterpreter(modelPath)
    }
    private val isRecording = MutableStateFlow(false)
    private val isFinishedRequestPostWorkoutData = MutableStateFlow(false)

    private val _uploadLoadingProgress = MutableStateFlow<Float?>(null)
    val uploadLoadingProgress = _uploadLoadingProgress.asStateFlow()

    init {
        MainApplication.appPreference.currentVideoFileDir = null

        viewModelScope.launch {
            launch(Dispatchers.IO) {
                requestGetExercises()
//                workoutNameList.clear()
//                workoutNameList.addAll(
//                    listOf(
//                        "푸쉬업",
//                        "데드리프트",
//                        "딥스",
//                        "벤치프레스",
//                        "숄더프레스"
//                    )
//                )
            }
            launch(Dispatchers.Default) {
                workoutInfo.collectLatest { info ->
                    if (info != null && !isWorkoutRunning.value) {
                        val isLogin = MainApplication.appPreference.isLogin

                        if (isLogin) {
                            requestPostWorkoutNote()
                        } else {
                            startFirstCountdownTimer()
                        }
                    }
                }
            }
            launch(Dispatchers.Default) {
                firstCountdown.map { countdown ->
                    countdown to null
                }.scan(-1 as Int? to -1 as Int?) { prev, next ->
                    prev.second to next.first
                }.collectLatest { (prevCountdown, nextCountdown) ->
                    if (prevCountdown == 0 && nextCountdown == null) {
                        stopFirstTimer()
                        startWorkoutTimer()

                        if (MainApplication.appPreference.isLogin)
                            _workoutUiEvent.emit(WorkoutUiEvent.StartRecording)
                    }
                }
            }
            launch(Dispatchers.Default) {
                restCountdown.map { countdown ->
                    countdown to null
                }.scan(-1 as Int? to -1 as Int?) { prev, next ->
                    prev.second to next.first
                }.collectLatest { (prevCountdown, nextCountdown) ->
                    if (prevCountdown == 0 && nextCountdown == null) {
                        stopRestTimer()
                        startWorkoutTimer()
                    }
                }
            }
            launch(Dispatchers.Default) {
                combine(updatedPose, workoutName) { pose, name ->
                    name?.run { pose.toLandmarkInfo(this) }
//                    pose.toLandmarkInfo(name)
                }.filter { landmarkInfo ->
                    landmarkInfo != null //  && isRecording
                }.map { landmarkInfo ->
                    landmarkInfo!!
                }.collectLatest { landmarkInfo ->
                    landmarkInfo.run {
                        // 반전
                        val isHeadToLeft = leftShoulder.x < leftAnkle.x
                        val leftArmAngle = calculateAngle(
                            pointA = leftShoulder.toPair(),
                            pointB = leftElbow.toPair(),
                            pointC = leftWrist.toPair()
                        )
                        val rightArmAngle = calculateAngle(
                            pointA = rightShoulder.toPair(),
                            pointB = rightElbow.toPair(),
                            pointC = rightWrist.toPair()
                        )
//                        val averageArmAngle = calculateAngle(
//                            pointA = Pair(
//                                (leftShoulder.x + rightShoulder.x) / 2f,
//                                (leftShoulder.y + rightShoulder.y) / 2f,
//                            ),
//                            pointB = Pair(
//                                (leftElbow.x + rightElbow.x) / 2f,
//                                (leftElbow.y + rightElbow.y) / 2f,
//                            ),
//                            pointC = Pair(
//                                (leftWrist.x + rightWrist.x) / 2f,
//                                (leftWrist.y + rightWrist.y) / 2f,
//                            )
//                        )

                        /**
                         * 확률 맥스 찾고 그거 인덱스 찾아서 범위 잡기
                         *
                         * 스쿼트
                         * 0 true 4개
                         * 1 ~ 4 true 3개
                         * 5 ~ 10 true 2개
                         * 11~14 true 1개
                         * 15 true 0개
                         * 무릎과 엉덩이 y가 같다고 판단하는 영역을 넘어갔다 올라오면 1회
                         * 허벅지 5분의 1부터 무릎 사이 영역 찍고 올라오면 1회
                         *
                         * 데드
                         * 0이 true 5개
                         * 1~5 true 4개
                         * 6~15 true 3개
                         * 16 ~ 25  true 2개
                         * 26~30 true 1개
                         * 31 true 0개
                         * 무릎 위 허벅지 5분의 1 영역 찍고 리턴
                         */

//                        val isHighest = if (isHeadToLeft) {
//                            leftArmAngle >= 165f
//                        } else {
//                            rightArmAngle >= 165f
//                        }
//                        val isLowest = if (isHeadToLeft) {
//                            leftArmAngle <= 105f
//                        } else {
//                            rightArmAngle <= 105f
//                        }

//                        val isHighest = if (isHeadToLeft) {
//                            rightArmAngle >= 165f
//                        } else {
//                            leftArmAngle >= 165f
//                        }
//                        val isLowest = if (isHeadToLeft) {
//                            rightArmAngle <= 105f
//                        } else {
//                            leftArmAngle <= 105f
//                        }
                        val isHighest = (leftArmAngle >= 165f || rightArmAngle >= 165f)
                        val isLowest = (leftArmAngle <= 105f || rightArmAngle <= 105f)
//                        val isHighest = averageArmAngle >= 165f
//                        val isLowest = averageArmAngle <= 105f

                        if (!isReachedFirstHighestPointPushUp.value) {
                            isReachedFirstHighestPointPushUp.value = // isHighest
                                isHighest && !isLowest
                        }

                        if (!isReachedLowestPointPushUp.value) {
                            isReachedLowestPointPushUp.value =
                                isReachedFirstHighestPointPushUp.value && // isLowest
                                        isLowest && !isHighest
                        }

                        if (!isReachedLastHighestPointPushUp.value) {
                            isReachedLastHighestPointPushUp.value =
                                (isReachedFirstHighestPointPushUp.value && isReachedLowestPointPushUp.value) &&
//                                        isHighest
                                        isHighest && !isLowest
                        }

//                        println("jaehoLee", "averageDegree = $averageArmAngle, ${isReachedFirstHighestPointPushUp.value}, ${isReachedLowestPointPushUp.value}, ${isReachedLastHighestPointPushUp.value}")
//                        println("jaehoLee", "leftDegree = $leftArmAngle, rightDegree = $rightArmAngle, ${isReachedFirstHighestPointPushUp.value}, ${isReachedLowestPointPushUp.value}, ${isReachedLastHighestPointPushUp.value}")
//                        if (isHeadToLeft)
//                            println("jaehoLee", "leftDegree = $leftArmAngle, ${isReachedFirstHighestPointPushUp.value}, ${isReachedLowestPointPushUp.value}, ${isReachedLastHighestPointPushUp.value}")
//                        else
//                            println("jaehoLee", "rightDegree = $rightArmAngle, ${isReachedFirstHighestPointPushUp.value}, ${isReachedLowestPointPushUp.value}, ${isReachedLastHighestPointPushUp.value}")
                    }
                }
            }
            launch(Dispatchers.Default) {
                combine(
                    isReachedFirstHighestPointPushUp,
                    isReachedLowestPointPushUp,
                    isReachedLastHighestPointPushUp
                ) { isFirstHighest, isLowest, isLastHighest ->
                    Triple(isFirstHighest, isLowest, isLastHighest)
                }.filter { (isFirstHighest, isLowest, isLastHighest) ->
                    isFirstHighest && isLowest && isLastHighest && firstTimer == null && restTimer == null
                }.collectLatest {
                    val frameDataList = if (landmarkInfoList.value.size > 8)
                        landmarkInfoList.value.takeLast(8)
                    else
                        landmarkInfoList.value
                    landmarkInfoList.value = listOf()
//                    val interpreter = interpreter.firstOrNull()
//                    val inputData = FloatArray(
//                        frameDataList.size * 12 * 2
//                    )
                    val inputData = getNormalizedFrameFloatArray(frameDataList)
                    val inputBuffer = ByteBuffer.allocateDirect(1 * 8 * 24 * 5 * Float.SIZE_BYTES).apply {
                        order(ByteOrder.nativeOrder())
                    }
                    inputData.forEach { value ->
                        inputBuffer.putFloat(value)
                    }

                    val outputBuffer = ByteBuffer.allocateDirect(32 * Float.SIZE_BYTES).apply {
                        order(ByteOrder.nativeOrder())
                    }

                    val interpreter = tensorFlowManager.getInterpreter("pushup_model.tflite")

                    interpreter.run(inputBuffer, outputBuffer)

                    outputBuffer.rewind()
                    val predictionList = FloatArray(32)
                    outputBuffer.asFloatBuffer().get(predictionList)

                    val highestPrediction = predictionList.toList().max()
                    val selectedPredictionIndex = predictionList.indexOfFirst { prediction ->
                        prediction == highestPrediction
                    }

                    println("jaehoLee", "selectedPrediction = ${predictionList.toList()}")
                    println("jaehoLee", "selectedPredictionIndex = $selectedPredictionIndex, $highestPrediction")

//                    frameDataList.forEach { frameData ->
//                        println("jaehoLee", "frameData: ${Json.encodeToString(frameData)}")
//                    }
                    /**
                     *                         0이 true 5개
                     *                         1~5 true 4개
                     *                         6~15 true 3개
                     *                         16~25  true 2개
                     *                         26~30 true 1개
                     *                         31 true 0개
                     */

//                    when (selectedPredictionIndex) {
//                        in 0..5 -> _scorePerfect.value += 1
//                        in 6..25 -> _scoreGood.value += 1
//                        in 26..31 -> _scoreNotGood.value += 1
//                    }
                    when (selectedPredictionIndex) {
                        in 0..15 -> _scorePerfect.value += 1
                        in 16..25 -> _scoreGood.value += 1
                        in 26..31 -> _scoreNotGood.value += 1
                    }

                    val currentWorkoutInfo = workoutInfo.value
                    workoutInfo.value = currentWorkoutInfo?.run {
                        copy(
                            setDataList = setDataList.mapIndexed { index, workoutData ->
                                if (index + 1 == currentSet.value) {
                                    workoutData.copy(
                                        scorePerfect = scorePerfect.value,
                                        scoreGood = scoreGood.value,
                                        scoreBad = scoreNotGood.value,
                                    )
                                } else {
                                    workoutData
                                }
                            }
                        )
                    }
//                    when (selectedPredictionIndex) {
//                        in 0..5 -> _scorePerfect.value += 1
//                        in 6..25 -> _scoreGood.value += 1
//                        in 26..31 -> _scoreNotGood.value += 1
//                    }

//                    interpreter.let { model ->
//                        val inputArray = arrayOf(floatArrayOf(32f))
//                        val outputArray = arrayOf(floatArrayOf(0f))
//
////                        model.run(inputArray, outputArray)
//
////                        println("jaehoLee", "inputShape = ${model.getInputTensor(0).shape()}")
////                        println("jaehoLee", "inputType = ${model.getInputTensor(0).dataType()}")
//
//                        outputArray[0].forEachIndexed { index, output ->
//                            println("jaehoLee", "output[$index] = $output")
//                        }
//                    }
                    inputBuffer.clear()
                    outputBuffer.clear()

                    isReachedFirstHighestPointPushUp.value = false
                    isReachedLowestPointPushUp.value = false
                    isReachedLastHighestPointPushUp.value = false
                }
            }
            launch(Dispatchers.Default) {
                totalCount.flatMapLatest { workoutCount ->
                    combine (currentSetCount, setAmount, currentSet) { count, amount, set ->
                        val isSetFinished = count == workoutCount
                        val isWorkoutFinished = isSetFinished && set == amount

                        isSetFinished to isWorkoutFinished
                    }.flatMapLatest { (isSetFinished, isWorkoutFinished) ->
                        workoutDataReq.map { req ->
                            Triple(isSetFinished, isWorkoutFinished, req)
                        }
                    }
                }.collectLatest { (isSetFinished, isWorkoutFinished, req) ->
                    if (!isWorkoutFinished) {
                        if (isSetFinished) {
                            stopWorkoutTimer()
                            startRestCountdownTimer()
                        }
                    } else {
                        stopWorkoutTimer()

                        if (MainApplication.appPreference.isLogin) {
                            requestPostWorkoutData(req)
                        } else {
                            onClickStopWorkout()
                        }
                    }
                }
            }
            launch(Dispatchers.Default) {
                combine(isRecording, isFinishedRequestPostWorkoutData) { isRecording, isFinished ->
                    isRecording to isFinished
                }.filter { (isRecording, isFinished) ->
                    !isRecording && isFinished
                }.map {
                    val noteId = noteId.value
                    val workoutName = workoutNameForVideo.value

                    noteId to workoutName
                }.filter { (noteId, workoutName) ->
                    noteId != -1 && workoutName.isNotEmpty()
                }.collectLatest { (noteId, workoutName) ->
                    requestPostWorkoutVideo(noteId, workoutName)
                }
            }
        }
    }

    fun setIsRecording(recording: Boolean) {
        println("jaehoLee", "setIsRecording, $recording")
        isRecording.value = recording
    }

    fun onClickRecordButtonWhenWorkoutInfoAlreadyExists() {
        viewModelScope.launch {
            startFirstCountdownTimer()
        }
    }

    fun onClickStopWorkout() {
        viewModelScope.launch {
            stopWorkoutTimer()
            stopFirstTimer()
            stopRestTimer()

            clearWorkoutData()
        }
    }

    private fun clearWorkoutData(onFinishClear: () -> Unit = { }) {
        println("jaehoLee", "clear")
        workoutInfo.value = null
        _currentSet.value = 0
        _isWorkoutRunning.value = false
        _scorePerfect.value = 0
        _scoreGood.value = 0
        _scoreNotGood.value = 0

        poseList.clear()
        _bodyFrameData.value = null

        workoutTimer?.cancel()
        firstTimer?.cancel()
        restTimer?.cancel()
        workoutTimer = null
        firstTimer = null
        restTimer = null

        currentWorkoutTime.value = 0L
        _firstCountdown.value = null
        _restCountdown.value = null

        viewModelScope.launch {
            if (MainApplication.appPreference.isLogin)
                _workoutUiEvent.emit(WorkoutUiEvent.StopRecording)
        }

        onFinishClear()
    }

    fun setWorkoutInfo(info: WorkoutInfo) {
        workoutInfo.value = info
    }

    private suspend fun startFirstCountdownTimer() {
        firstTimer = timer(period = 1000L) {
            val currentCountdown = firstCountdown.value

            _firstCountdown.value = if (currentCountdown == null) {
                10
            } else {
                if (currentCountdown != 0)
                    currentCountdown - 1
                else
                    null
            }
        }
    }

    private suspend fun startRestCountdownTimer() {
        val restTime = restTime.firstOrNull() ?: 30

        restTimer = timer(period = 1000L) {
            val currentCountdown = restCountdown.value

            _restCountdown.value = if (currentCountdown == null) {
                restTime
            } else {
                if (currentCountdown != 0)
                    currentCountdown - 1
                else {
                    _scorePerfect.value = 0
                    _scoreGood.value = 0
                    _scoreNotGood.value = 0
                    null
                }
            }
        }
    }

    private suspend fun startWorkoutTimer() {
        workoutTimer = timer(period = 500L) {
            currentWorkoutTime.value += 500L
        }

        _currentSet.value += 1
        _isWorkoutRunning.value = true
    }

    private fun stopWorkoutTimer() {
        workoutTimer?.cancel()
        currentWorkoutTime.value = 0L
//        _isWorkoutRunning.value = false
    }

    private fun stopFirstTimer() {
        firstTimer?.cancel()
        firstTimer = null
    }

    private fun stopRestTimer() {
        restTimer?.cancel()
        restTimer = null
    }

    fun onUpdatePose(
        cameraWidthPx: Int,
        cameraHeightPx: Int,
        imageWidth: Int = 480,
        imageHeight: Int = 640,
        pose: Pose,
        copy: (String) -> Unit
    ) {
        viewModelScope.launch {
//            if (!isLoadingFinishWorkout.value) {
//                updatedPose.emit(pose)
//            }
            updatedPose.emit(pose)

            if (poseList.size == 2 && !isLoadingFinishWorkout.value) {
                updateBodyFrameData(
                    cameraWidthPx = cameraWidthPx,
                    cameraHeightPx = cameraHeightPx,
                    imageWidth = imageWidth,
                    imageHeight = imageHeight,
                    landmarkPositionList = getAverageLandmarkPositionList()
                )
                poseList.clear()
            }

            poseList.add(pose)

//            if (!isLoadingFinishWorkout.value) {
//                poseList.add(pose)
//            }
        }
    }

    private fun getAverageLandmarkPositionList(): List<Pair<Float, Float>> {
        val processedPoseList = poseList.map { savedPose ->
            savedPose.allPoseLandmarks.filter { landmark ->
                landmark.landmarkType >= PoseLandmark.LEFT_SHOULDER
            }.sortedBy { landmark ->
                landmark.landmarkType
            }.map { landmark ->
                landmark.position.run { x to y }
            }
        }

        return if (processedPoseList.isNotEmpty()) {
            processedPoseList.first().mapIndexed { index, (x, y) ->
                var resultX = x
                var resultY = y

                for (i in 1 until processedPoseList.size) {
                    val (nextX, nextY) = processedPoseList[i][index]

                    resultX += nextX
                    resultY += nextY
                }

                resultX / processedPoseList.size.toFloat() to resultY / processedPoseList.size.toFloat()
            }
        } else {
            listOf()
        }
    }
    private suspend fun updateBodyFrameData(
        cameraWidthPx: Int,
        cameraHeightPx: Int,
        imageWidth: Int,
        imageHeight: Int,
        landmarkPositionList: List<Pair<Float, Float>>
    ) = withContext(Dispatchers.Default) {
        if (landmarkPositionList.isNotEmpty()) {
            val cameraWidthFloat = cameraWidthPx.toFloat()
            val cameraHeightFloat = cameraHeightPx.toFloat()
            val imageWidthFloat = imageWidth.toFloat()
            val imageHeightFloat = imageHeight.toFloat()

            /**
             *    (imageWidth - invisibleImageWidth) / imageHeight = cameraWidth / cameraHeight
             * => cameraHeight * (imageWidth - invisibleImageWidth) = cameraWidth * imageHeight
             * => imageWidth - invisibleImageWidth = (cameraWidth * imageHeight) / cameraHeight
             * => imageWidth - ((cameraWidth * imageHeight) / cameraHeight) = invisibleImageWidth
             * => invisibleImageWidth = imageWidth - ((cameraWidth  g* imageHeight) / cameraHeight)
             */
            val invisibleImageWidth = imageWidthFloat - ((cameraWidthFloat * imageHeightFloat) / cameraHeightFloat)
            val invisibleImageWidthHalf = invisibleImageWidth / 2f

            val processedOffsetList = landmarkPositionList.filter { (x, _) ->
                val isInVisibleArea = x in invisibleImageWidthHalf..(imageWidthFloat - invisibleImageWidthHalf)

                isInVisibleArea
            }.map { (x, y) ->
                // 좌우 반전 수정
//                val widthRatio = (x - invisibleImageWidthHalf) / (imageWidthFloat - invisibleImageWidth)
                val widthRatio = ((imageWidth - x) - invisibleImageWidthHalf) / (imageWidthFloat - invisibleImageWidth)
                val heightRatio = y / imageHeightFloat
                Offset(
                    x = cameraWidthPx * widthRatio,
                    y = cameraHeightPx * heightRatio
                )
            }.toList()

            val getLandmarkByIndex: (Int) -> Offset? = { index ->
                processedOffsetList.getOrNull(index - PoseLandmark.LEFT_SHOULDER)
            }

            val data = BodyFrameData(
                offsetList = processedOffsetList.run {
                    listOf(
                        // Left Arm
                        getLandmarkByIndex(PoseLandmark.LEFT_SHOULDER) to
                                getLandmarkByIndex(PoseLandmark.LEFT_ELBOW),
                        getLandmarkByIndex(PoseLandmark.LEFT_ELBOW) to
                                getLandmarkByIndex(PoseLandmark.LEFT_WRIST),
                        getLandmarkByIndex(PoseLandmark.LEFT_WRIST) to
                                getLandmarkByIndex(PoseLandmark.LEFT_PINKY),
                        getLandmarkByIndex(PoseLandmark.LEFT_WRIST) to
                                getLandmarkByIndex(PoseLandmark.LEFT_INDEX),
                        getLandmarkByIndex(PoseLandmark.LEFT_WRIST) to
                                getLandmarkByIndex(PoseLandmark.LEFT_THUMB),
                        getLandmarkByIndex(PoseLandmark.LEFT_PINKY) to
                                getLandmarkByIndex(PoseLandmark.LEFT_INDEX),

                        // Right Arm
                        getLandmarkByIndex(PoseLandmark.RIGHT_SHOULDER) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_ELBOW),
                        getLandmarkByIndex(PoseLandmark.RIGHT_ELBOW) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_WRIST),
                        getLandmarkByIndex(PoseLandmark.RIGHT_WRIST) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_PINKY),
                        getLandmarkByIndex(PoseLandmark.RIGHT_WRIST) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_INDEX),
                        getLandmarkByIndex(PoseLandmark.RIGHT_WRIST) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_THUMB),
                        getLandmarkByIndex(PoseLandmark.RIGHT_PINKY) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_INDEX),

                        // Body
                        getLandmarkByIndex(PoseLandmark.LEFT_SHOULDER) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_SHOULDER),
                        getLandmarkByIndex(PoseLandmark.LEFT_HIP) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_HIP),
                        getLandmarkByIndex(PoseLandmark.LEFT_SHOULDER) to
                                getLandmarkByIndex(PoseLandmark.LEFT_HIP),
                        getLandmarkByIndex(PoseLandmark.RIGHT_SHOULDER) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_HIP),

                        // Left Leg
                        getLandmarkByIndex(PoseLandmark.LEFT_HIP) to
                                getLandmarkByIndex(PoseLandmark.LEFT_KNEE),
                        getLandmarkByIndex(PoseLandmark.LEFT_KNEE) to
                                getLandmarkByIndex(PoseLandmark.LEFT_ANKLE),
                        getLandmarkByIndex(PoseLandmark.LEFT_ANKLE) to
                                getLandmarkByIndex(PoseLandmark.LEFT_HEEL),
                        getLandmarkByIndex(PoseLandmark.LEFT_ANKLE) to
                                getLandmarkByIndex(PoseLandmark.LEFT_FOOT_INDEX),
                        getLandmarkByIndex(PoseLandmark.LEFT_HEEL) to
                                getLandmarkByIndex(PoseLandmark.LEFT_FOOT_INDEX),

                        // Right Leg
                        getLandmarkByIndex(PoseLandmark.RIGHT_HIP) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_KNEE),
                        getLandmarkByIndex(PoseLandmark.RIGHT_KNEE) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_ANKLE),
                        getLandmarkByIndex(PoseLandmark.RIGHT_ANKLE) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_HEEL),
                        getLandmarkByIndex(PoseLandmark.RIGHT_ANKLE) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_FOOT_INDEX),
                        getLandmarkByIndex(PoseLandmark.RIGHT_HEEL) to
                                getLandmarkByIndex(PoseLandmark.RIGHT_FOOT_INDEX)
                    ).filter { (start, end) ->
                        start != null && end != null
                    }.map { (start, end) ->
                        start!! to end!!
                    }
                }
            )
            _bodyFrameData.value = data
        }
    }

    private suspend fun requestGetExercises() {
        ApiRequestHelper.makeRequest {
            exercisesRepository.getExercises()
        }.onSuccess { res ->
            if (!(res.result?.exerciseList.isNullOrEmpty())) {
                val dataList = res.result!!.exerciseList.map { exerciseData ->
                    exerciseData.run {
                        exerciseName to (exerciseType == WorkoutType.BodyWeight.value)
                    }
                }

                workoutDataList.clear()
                workoutDataList.addAll(dataList)
            }
        }.onFailure { res ->
            println("jaehoLee", "onFailure: (${res.code}), ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError: ${throwable.message}")
        }
    }

    private suspend fun requestPostWorkoutNote() {
        ApiRequestHelper.makeRequest {
            workoutRepository.postWorkoutNote(MainApplication.appPreference.userId)
        }.onSuccess { res ->
            println("jaehoLee", "noteId Before = ${noteId.value}")
            noteId.value = res.result?.noteId ?: -1
            println("jaehoLee", "noteId After = ${noteId.value}")

            startFirstCountdownTimer()
        }.onFailure { res ->
            println("jaehoLee", "onFailure of postWorkoutNote(): (${res.code}), ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError of postWorkoutNote(): ${throwable.message}")
        }
    }

    private suspend fun requestPostWorkoutData(req: PostWorkoutDataReq) {
        ApiRequestHelper.makeRequest {
            workoutRepository.postWorkoutData(req)
        }.onSuccess {
            workoutNameForVideo.value = workoutName.firstOrNull() ?: ""
            clearWorkoutData()
            isFinishedRequestPostWorkoutData.value = true
        }.onFailure { res ->
            println("jaehoLee", "onFailure of postWorkoutData(): (${res.code}), ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError of postWorkoutData(): ${throwable.message}")
        }
    }

    private suspend fun requestPostWorkoutVideo(
        noteId: Int,
        workoutName: String
    ) {
        println("jaehoLee", "noteId in request = $noteId")
        var videoFileDir = MainApplication.appPreference.currentVideoFileDir

        while (videoFileDir == null) {
            videoFileDir = MainApplication.appPreference.currentVideoFileDir
        }

        _isLoadingFinishWorkout.value = true

        ApiRequestHelper.makeRequest {
            workoutRepository.postWorkoutVideo(
                noteId = noteId,
                exerciseName = workoutName,
                videoFileDir = videoFileDir,
                onProgress = { progress, contentLength ->
                    progress.toFloat() / contentLength.toFloat()
                    _uploadLoadingProgress.value = progress.toFloat() / contentLength.toFloat()
                }
            )
        }.onSuccess { res ->
            try {
                val file = File(videoFileDir)

                if(file.exists()){
                    file.delete();
                }

                println("jaehoLee", "isExistAfterDelete = ${file.exists()}")
            } catch (e: Throwable){
                println("jaehoLee", "error in file delete: ${e.message}")
            }
            println("jaehoLee", "onSuccess(postWorkoutVideo()): ${res.message}}")
            workoutNameForVideo.value = ""
            clearWorkoutData(
                onFinishClear = {
                    viewModelScope.launch {
                        _workoutUiEvent.emit(WorkoutUiEvent.FinishWorkout(noteId))
                        _isLoadingFinishWorkout.value = false
                    }
                }
            )
        }.onFailure { res ->
            try {
                val file = File(videoFileDir)

                if(file.exists()){
                    file.delete();
                }

                println("jaehoLee", "isExistAfterDelete = ${file.exists()}")
            } catch (e: Throwable){
                println("jaehoLee", "error in file delete: ${e.message}")
            }
            workoutNameForVideo.value = ""
            clearWorkoutData(
                onFinishClear = {
                    viewModelScope.launch {
                        _workoutUiEvent.emit(WorkoutUiEvent.FinishWorkout(noteId))
                        _isLoadingFinishWorkout.value = false
                    }
                }
            )
            println("jaehoLee", "onFailure of postWorkoutVideo(): (${res.code}), ${res.message}")
        }.onError { throwable ->
            try {
                val file = File(videoFileDir)

                if(file.exists()){
                    file.delete();
                }

                println("jaehoLee", "isExistAfterDelete = ${file.exists()}")
            } catch (e: Throwable){
                println("jaehoLee", "error in file delete: ${e.message}")
            }
            workoutNameForVideo.value = ""
            clearWorkoutData(
                onFinishClear = {
                    viewModelScope.launch {
                        _workoutUiEvent.emit(WorkoutUiEvent.FinishWorkout(noteId))
                        _isLoadingFinishWorkout.value = false
                    }
                }
            )
            println("jaehoLee", "onError of postWorkoutVideo(): ${throwable.message}")
        }
    }

    sealed class WorkoutUiEvent {
        data object StartRecording : WorkoutUiEvent()
        data object StopRecording : WorkoutUiEvent()
        data class FinishWorkout(val noteId: Int) : WorkoutUiEvent()
    }
}