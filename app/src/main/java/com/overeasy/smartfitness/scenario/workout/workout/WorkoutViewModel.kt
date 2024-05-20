@file:OptIn(ExperimentalCoroutinesApi::class)

package com.overeasy.smartfitness.scenario.workout.workout

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.calculateAngle
import com.overeasy.smartfitness.domain.ai.AiRepository
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.ai.entity.PostAiReq
import com.overeasy.smartfitness.domain.ai.model.LandmarkInfo
import com.overeasy.smartfitness.domain.exercises.ExercisesRepository
import com.overeasy.smartfitness.domain.workout.entity.PostWorkoutDataReq
import com.overeasy.smartfitness.domain.workout.model.workout.WorkoutData
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

    private val _workoutNameList = mutableStateListOf<String>("푸쉬업", "데드리프트", "딥스", "스쿼트", "숄더프레스")
    val workoutNameList = _workoutNameList

    private val _bodyFrameData = MutableStateFlow<BodyFrameData?>(null)
    val bodyFrameData = _bodyFrameData.asStateFlow()

    private val noteId = MutableStateFlow(-1)
    private val workoutInfo = MutableStateFlow<WorkoutInfo?>(null)
    private val workoutName = workoutInfo.filterNotNull().map { info ->
        info.workoutName
    }
    private val restTime = workoutInfo.filter { info ->
        info?.restTime != null
    }.map { info ->
        info?.restTime!!
    }
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

    private val workoutFrameDataList = MutableStateFlow<List<LandmarkInfo>>(listOf())

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

    init {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
//                requestGetExercises() // 관리자 데이터 삽입 후 주석 해제
            }
            launch(Dispatchers.Default) {
                workoutInfo.collectLatest { info ->
                    println("jaehoLee", "info = $info")
                    workoutTimer = timer(period = 400L) {
                        currentWorkoutTime.value += 400L
                    }
                    if (info != null) {
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
//                combine(updatedPose, workoutName) { pose, name ->
//                    pose.toLandmarkInfo(name)
//                }.filterNotNull().collectLatest { landmarkInfo ->
                currentWorkoutTime.map {
                    updatedPose.firstOrNull()
                }.filterNotNull().map { pose ->
                    pose.toLandmarkInfo("푸시업")
                }.filterNotNull().collectLatest { landmarkInfo ->
                    landmarkInfo.run {
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

                        /*
                        확률 맥스 찾고 그거 인덱스 찾아서 범위 잡기

                        스쿼트
                        0 true 4개
                        1 ~ 4 true 3개
                        5 ~ 10 true 2개
                        11~14 true 1개
                        15 true 0개
                        무릎과 엉덩이 y가 같다고 판단하는 영역을 넘어갔다 올라오면 1회
                        허벅지 5분의 1부터 무릎 사이 영역 찍고 올라오면 1회

                        데드
                        0이 true 5개
                        1~5 true 4개
                        6~15 true 3개
                        16 ~ 25  true 2개
                        26~30 true 1개
                        31 true 0개
                        무릎 위 허벅지 5분의 1 영역 찍고 리턴
                         */
                        val isHighest = (leftArmAngle >= 165f || rightArmAngle >= 165f)
                        val isLowest = (leftArmAngle <= 105f || rightArmAngle <= 105f)

                        if (!isReachedFirstHighestPointPushUp.value) {
                            isReachedFirstHighestPointPushUp.value =
                                isHighest && !isLowest
                        }

                        if (!isReachedLowestPointPushUp.value) {
                            isReachedLowestPointPushUp.value =
                                isReachedFirstHighestPointPushUp.value &&
                                        isLowest && !isHighest
                        }

                        if (!isReachedLastHighestPointPushUp.value) {
                            isReachedLastHighestPointPushUp.value =
                                (isReachedFirstHighestPointPushUp.value && isReachedLowestPointPushUp.value) &&
                                        isHighest && !isLowest
                        }

                        landmarkInfoList.value += landmarkInfo

//                        println("jaehoLee", "leftDegree = $leftArmAngle, rightDegree = $rightArmAngle, ${isReachedFirstHighestPoint.value}, ${isReachedLowestPoint.value}, ${isReachedLastHighestPoint.value}")
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
                    val inputData = FloatArray(
                        frameDataList.size * 12 * 2
                    )

                    frameDataList.forEachIndexed { index, landmarkInfo ->
                        val expandedIndex = (index + 1) * 24

                        inputData[expandedIndex - 24] = landmarkInfo.leftShoulder.x
                        inputData[expandedIndex - 23] = landmarkInfo.leftShoulder.y
                        inputData[expandedIndex - 22] = landmarkInfo.rightShoulder.x
                        inputData[expandedIndex - 21] = landmarkInfo.rightShoulder.y
                        inputData[expandedIndex - 20] = landmarkInfo.leftElbow.x
                        inputData[expandedIndex - 19] = landmarkInfo.leftElbow.y
                        inputData[expandedIndex - 18] = landmarkInfo.rightElbow.x
                        inputData[expandedIndex - 17] = landmarkInfo.rightElbow.y
                        inputData[expandedIndex - 16] = landmarkInfo.leftWrist.x
                        inputData[expandedIndex - 15] = landmarkInfo.leftWrist.y
                        inputData[expandedIndex - 14] = landmarkInfo.rightWrist.x
                        inputData[expandedIndex - 13] = landmarkInfo.rightWrist.y
                        inputData[expandedIndex - 12] = landmarkInfo.leftHip.x
                        inputData[expandedIndex - 11] = landmarkInfo.leftHip.y
                        inputData[expandedIndex - 10] = landmarkInfo.rightHip.x
                        inputData[expandedIndex - 9] = landmarkInfo.rightHip.y
                        inputData[expandedIndex - 8] = landmarkInfo.leftKnee.x
                        inputData[expandedIndex - 7] = landmarkInfo.leftKnee.y
                        inputData[expandedIndex - 6] = landmarkInfo.rightKnee.x
                        inputData[expandedIndex - 5] = landmarkInfo.rightKnee.y
                        inputData[expandedIndex - 4] = landmarkInfo.leftAnkle.x
                        inputData[expandedIndex - 3] = landmarkInfo.leftAnkle.y
                        inputData[expandedIndex - 2] = landmarkInfo.rightAnkle.x
                        inputData[expandedIndex - 1] = landmarkInfo.rightAnkle.y
                    }
                    val inputBuffer = ByteBuffer.allocateDirect(1 * 8 * 24 * Float.SIZE_BYTES).apply {
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

                    val selectedPredictionIndex = predictionList.indexOfFirst { prediction ->
                        prediction > 0.0f
                    }

                    val trueCount = frameDataList.map { landmarkInfo ->
                        landmarkInfo.run {
                            listOf(
                                (leftShoulder.y < leftHip.y && leftHip.y < leftKnee.y),
                                (leftElbow.x < leftWrist.x),
                                (leftShoulder.x < leftElbow.x),
                                (leftWrist.y > leftShoulder.y),
                                (leftShoulder.y < leftElbow.y)
                            ).count { isSatisfied ->
                                isSatisfied
                            }
                        }
                    }.sumOf { trueCount ->
                        trueCount
                    } / frameDataList.size

                    println("jaehoLee", "trueCount = $trueCount")

                    /**
                     *
                     *                         0이 true 5개
                     *                         1~5 true 4개
                     *                         6~15 true 3개
                     *                         16 ~ 25  true 2개
                     *                         26~30 true 1개
                     *                         31 true 0개
                     */

                    when (trueCount) {
                        in 3..4 -> _scorePerfect.value += 1
                        in 1..2 -> _scoreGood.value += 1
                        0 -> _scoreNotGood.value += 1
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

//                    println("jaehoLee", "degree job done, ${tempList.size}")
//                    tempList.forEachIndexed { index, info ->
//                        info.workoutName
//                        println("jaehoLee", "degreeList[$index] = ${Json.encodeToString(info)}")
//                    }
                    isReachedFirstHighestPointPushUp.value = false
                    isReachedLowestPointPushUp.value = false
                    isReachedLastHighestPointPushUp.value = false
                }
            }
            launch(Dispatchers.Default) {
//                combine(updatedPose, workoutName) { pose, name ->
//                    pose to name
//                }.map { (pose, name) ->
//                    pose.toLandmarkInfo(name)
//                }.filterNotNull().flatMapLatest { req ->
//                    currentWorkoutTime.map {
//                        PostAiReq(
//                            positionList = listOf(
//                                req,
//                                req,
//                                req,
//                                req,
//                                req,
//                                req
//                            )
//                        )
//                    }
//                }.collectLatest { req ->
////                    requestPostWorkout(req)
//                }
//                currentWorkoutTime.flatMapLatest {
//                    combine(updatedPose, workoutName) { pose, name ->
//                        pose to name
//                    }.map { (pose, name) ->
//                        pose.toLandmarkInfo(name)
//                    }
//                }.filterNotNull().collectLatest { req ->
//                    requestPostWorkout(
//                        PostAiReq(
//                            positionList = listOf(
//                                req,
//                                req,
//                                req,
//                                req,
//                                req,
//                                req,
//                                req
//                            )
//                        )
//                    )
//                }
            }
            launch(Dispatchers.Default) {
                totalCount.flatMapLatest { workoutCount ->
                    combine(currentSetCount, setAmount, workoutDataReq) { count, amount, req ->
                        Triple(count, amount, req)
                    }.map { (count, amount, req) ->
                        val isSetFinished = count == workoutCount
                        val isWorkoutFinished = isSetFinished && currentSet.value == amount

                        println("jaehoLee", "count = $count, wCount = $workoutCount, amount = $amount, isSetFinished = $isSetFinished, isWorkoutFinished = $isWorkoutFinished")
                        Triple(isSetFinished, isWorkoutFinished, req)
                    }
                }.collectLatest { (isSetFinished, isWorkoutFinished, req) ->
                    if (!isWorkoutFinished) {
                        if (isSetFinished) {
                            stopWorkoutTimer()
                            startRestCountdownTimer()
                        }
                    } else {
                        val workoutName = workoutName.firstOrNull() ?: ""

                        stopWorkoutTimer()

                        if (workoutName.isNotEmpty() && noteId.value != -1) {
                            _isLoadingFinishWorkout.value = true
                            requestPostWorkoutVideo(
                                noteId = noteId.value,
                                workoutName = workoutName
                            )
                        }
//                        if (MainApplication.appPreference.isLogin)
//                            onClickStopWorkout()
//                        else
//                            requestPostWorkoutData(req)
                        requestPostWorkoutData(req)
//                        delay(1000L)
//                        _workoutUiEvent.emit(WorkoutUiEvent.FinishWorkout)
                    }
                }
            }
        }
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
                5
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
        workoutTimer = timer(period = 300L) {
            currentWorkoutTime.value += 300L
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
        // 3개 평균치 내서 한 번에 하기
        viewModelScope.launch {
            // 저장용
//            launch(Dispatchers.IO) {
//                saveData(pose, copy)
//            }
            updatedPose.emit(pose)
            if (poseList.size == 2) {
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
        }
    }

    private suspend fun getAverageLandmarkPositionList(): List<Pair<Float, Float>> = withContext(Dispatchers.Default){
        val processedPoseList = poseList.map { savedPose ->
            savedPose.allPoseLandmarks.filter { landmark ->
                landmark.landmarkType >= PoseLandmark.LEFT_SHOULDER
            }.sortedBy { landmark ->
                landmark.landmarkType
            }.map { landmark ->
                landmark.position.run { x to y }
            }
        }

        if (processedPoseList.isNotEmpty()) {
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
             * => invisibleImageWidth = imageWidth - ((cameraWidth * imageHeight) / cameraHeight)
             */
            val invisibleImageWidth = imageWidthFloat - ((cameraWidthFloat * imageHeightFloat) / cameraHeightFloat)
            val invisibleImageWidthHalf = invisibleImageWidth / 2f

            val processedOffsetList = landmarkPositionList.filter { (x, _) ->
                val isInVisibleArea = x in invisibleImageWidthHalf..(imageWidthFloat - invisibleImageWidthHalf)

                isInVisibleArea
            }.map { (x, y) ->
                // 좌우 반전 수정
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

    private suspend fun saveData(pose: Pose, copy: (String) -> Unit) {
        if (pose.allPoseLandmarks.size != PoseLandmark.RIGHT_FOOT_INDEX) {
            pose.toLandmarkInfo("push_up")?.let { req ->
                val json = Gson().toJson(req)

                copy(json)
            }
        }
    }

    private suspend fun requestGetExercises() {
        ApiRequestHelper.makeRequest {
            exercisesRepository.getExercises()
        }.onSuccess { res ->
            if (!(res.result?.exerciseList.isNullOrEmpty())) {
                val nameList = res.result!!.exerciseList.map { exerciseData ->
                    exerciseData.exerciseName
                }

                workoutNameList.clear()
                workoutNameList.addAll(nameList)
            }
        }.onFailure { res ->
            println("jaehoLee", "onFailure: (${res.code}), ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError: ${throwable.message}")
        }
    }

    private suspend fun requestPostWorkoutNote() {
        ApiRequestHelper.makeRequest {
            println("jaehoLee", "userId = ${MainApplication.appPreference.userId}")
            workoutRepository.postWorkoutNote(MainApplication.appPreference.userId)
        }.onSuccess { res ->
            noteId.value = res.result?.noteId ?: -1

            startFirstCountdownTimer()
        }.onFailure { res ->
            println("jaehoLee", "onFailure(postWorkoutNote()): (${res.code}), ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError(postWorkoutNote()): ${throwable.message}")
        }
    }

    private suspend fun requestPostWorkout(req: PostAiReq) {
        ApiRequestHelper.makeRequest {
            aiRepository.postAi(req) // 임시
        }.onSuccess { res ->
            /* no-op */
        }.onFailure { res ->
            println("jaehoLee", "onFailure: (${res.code}), ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError: ${throwable.message}")
        }
    }

    private suspend fun requestPostWorkoutData(req: PostWorkoutDataReq) {
        ApiRequestHelper.makeRequest {
            workoutRepository.postWorkoutData(req)
        }.onSuccess {
            clearWorkoutData(
                onFinishClear = {
                    viewModelScope.launch {
                        _workoutUiEvent.emit(WorkoutUiEvent.FinishWorkout(noteId.value))
                    }
                }
            )
        }.onFailure { res ->
            println("jaehoLee", "onFailure(postWorkoutData()): (${res.code}), ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError(postWorkoutData()): ${throwable.message}")
        }
    }

    private suspend fun requestPostWorkoutVideo(
        noteId: Int,
        workoutName: String
    ) {
        ApiRequestHelper.makeRequest {
            workoutRepository.postWorkoutVideo(
                noteId = noteId,
                exerciseName = workoutName
            )
        }.onSuccess {
            MainApplication.appPreference.currentVideoFileDir?.let { videoDir ->
                try {
                    val file = File(videoDir)

                    if(file.exists()){
                        file.delete();
                    }

                    println("jaehoLee", "isExistAfterDelete = ${file.exists()}")
                } catch (e: Throwable){
                    println("jaehoLee", "error in file delete: ${e.message}")
                }
            }
            clearWorkoutData(
                onFinishClear = {
                    viewModelScope.launch {
                        _workoutUiEvent.emit(WorkoutUiEvent.FinishWorkout(noteId))
                        _isLoadingFinishWorkout.value = false
                    }
                }
            )
        }.onFailure { res ->
            println("jaehoLee", "onFailure(postWorkoutVideo()): (${res.code}), ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError(postWorkoutVideo()): ${throwable.message}")
        }
    }

    sealed class WorkoutUiEvent {
        data object StartRecording : WorkoutUiEvent()
        data object StopRecording : WorkoutUiEvent()
        data class FinishWorkout(val noteId: Int) : WorkoutUiEvent()
    }
}