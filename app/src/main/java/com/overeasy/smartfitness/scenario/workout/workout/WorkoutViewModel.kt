@file:OptIn(ExperimentalCoroutinesApi::class)

package com.overeasy.smartfitness.scenario.workout.workout

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.calculateAngle
import com.overeasy.smartfitness.domain.ai.AiRepository
import com.overeasy.smartfitness.domain.ai.model.LandmarkInfo
import com.overeasy.smartfitness.domain.base.makeRequest
import com.overeasy.smartfitness.domain.exercises.ExercisesRepository
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.dto.req.PostWorkoutDataReq
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timer
import kotlin.math.floor


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

    private val _workoutDataList = mutableStateListOf(
        "푸시업" to true,
        "데드리프트" to false,
        "스쿼트" to false,
    )
//    private val _workoutDataList = mutableStateListOf<Pair<String, Boolean>>()
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
            setData.run {
                if (info.workoutName != SupportedWorkout.PUSH_UP.value) {
                    weight != null && repeats != null
                } else {
                    repeats != null
                }
            }
        }
    }

    private val _currentSet = MutableStateFlow(0)
    val currentSet = _currentSet.asStateFlow()
    private val currentMaxSet = setDataList.map { list ->
        list?.size
    }

    private val poseList = mutableStateListOf<Pose>()

    private val updatedPose = MutableSharedFlow<Pose>()

    private val workoutCountBase = combine(updatedPose, workoutName) { pose, name ->
        name?.run { pose.toLandmarkInfo(this) } to name
    }.filter { (landmarkInfo, _) ->
        landmarkInfo != null &&
                firstTimer == null &&
                restTimer == null
    }.map { (landmarkInfo, name) ->
        landmarkInfo!! to name
    }

    private val _scorePerfect = MutableStateFlow(0)
    val scorePerfect = _scorePerfect.asStateFlow()
    private val _scoreGood = MutableStateFlow(0)
    val scoreGood = _scoreGood.asStateFlow()
    private val _scoreNotGood = MutableStateFlow(0)
    val scoreNotGood = _scoreNotGood.asStateFlow()

    private val currentCount = combine(scorePerfect, scoreGood, scoreNotGood) { perfect, good, notGood ->
        perfect + good + notGood
    }
    private val currentMaxCount = combine(setDataList, currentSet) { list, set ->
        list to set
    }.filter { (list, set) ->
        list?.isNotEmpty() == true && set > 0
    }.map { (list, set) ->
        list?.get(set - 1)?.repeats
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
                setData.run {
                    if (workoutInfo.workoutName != SupportedWorkout.PUSH_UP.value) {
                        weight != null && repeats != null
                    } else {
                        repeats != null
                    }
                }
            }.sortedBy { setData ->
                setData.setNum
            }
        )
    }

    private val isSetFinished = combine(currentCount, currentMaxCount) { count, maxCount ->
        count == maxCount
    }.distinctUntilChanged()

    private val isWorkoutFinished = combine(isSetFinished, currentSet, currentMaxSet) { isSetFinished, set, maxSet ->
        isSetFinished && set == maxSet
    }.distinctUntilChanged()

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

    private val isReachedFirstPoint = MutableStateFlow(false)
    private val isReachedMiddlePoint = MutableStateFlow(false)
    private val isReachedLastPoint = MutableStateFlow(false)

    private val landmarkInfoList = MutableStateFlow<List<LandmarkInfo>>(listOf())

    private var interpreter: MutableStateFlow<Interpreter?> = MutableStateFlow(null)

    private val isRecording = MutableStateFlow(false)
    private val isFinishedRequestPostWorkoutData = MutableStateFlow(false)

    private val _uploadLoadingProgress = MutableStateFlow<Float?>(null)
    val uploadLoadingProgress = _uploadLoadingProgress.asStateFlow()

    private val workoutResultIndexList = MutableStateFlow<ArrayList<ArrayList<Int>>>(arrayListOf())

    init {
        MainApplication.appPreference.currentVideoFileDir = null

        viewModelScope.launch {
            launch(Dispatchers.IO) {
                requestGetExercises()
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
                workoutName.distinctUntilChanged().map { name ->
                    when (name) {
                        SupportedWorkout.PUSH_UP.value -> "pushup_model.tflite"
                        SupportedWorkout.DEAD_LIFT.value -> "deadlift_model.tflite"
                        SupportedWorkout.SQUAT.value -> "squat_model.tflite"
                        else -> null
                    }
                }.filter { modelPath ->
                    modelPath != null
                }.map { modelPath ->
                    modelPath!!
                }.collectLatest { modelPath ->
                    interpreter.value = tensorFlowManager.getInterpreter(modelPath)
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
                        else
                            _workoutUiEvent.emit(WorkoutUiEvent.StartFakeRecording)
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
            // 푸시업 1회 카운트
            launch(Dispatchers.Default) {
                workoutCountBase.filter { (_, name) ->
                    name == SupportedWorkout.PUSH_UP.value
                }.map { (landmarkInfo, _) ->
                    landmarkInfo
                }.collectLatest { landmarkInfo ->
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

                        val isHighest = (leftArmAngle >= 165f || rightArmAngle >= 165f)
                        val isLowest = (leftArmAngle <= 105f || rightArmAngle <= 105f)

                        updateReachedPointState(
                            firstAndLastPointState = isHighest && !isLowest,
                            middlePointState = !isHighest && isLowest
                        )

                        landmarkInfoList.value += landmarkInfo

//                        println("jaehoLee", "leftDegree = $leftArmAngle, rightDegree = $rightArmAngle, ${isReachedFirstPoint.value}, ${isReachedMiddlePoint.value}, ${isReachedLastPoint.value}")
                    }
                }
            }
            // 스쿼트 1회 카운트
            launch(Dispatchers.Default) {
                workoutCountBase.filter { (_, name) ->
                    name == SupportedWorkout.SQUAT.value
                }.map { (landmarkInfo, _) ->
                    landmarkInfo
                }.collectLatest { landmarkInfo ->
                    landmarkInfo.run {
                        val leftLegAngle = calculateAngle(
                            pointA = leftHip.toPair(),
                            pointB = leftKnee.toPair(),
                            pointC = leftAnkle.toPair()
                        )
                        val rightLegAngle = calculateAngle(
                            pointA = rightHip.toPair(),
                            pointB = rightKnee.toPair(),
                            pointC = rightAnkle.toPair()
                        )
                        val leftWaistAngle = calculateAngle(
                            pointA = leftShoulder.toPair(),
                            pointB = leftHip.toPair(),
                            pointC = leftKnee.toPair()
                        )
                        val rightWaistAngle = calculateAngle(
                            pointA = rightShoulder.toPair(),
                            pointB = rightHip.toPair(),
                            pointC = rightKnee.toPair()
                        )

                        val isUp = (leftLegAngle > 160f && leftWaistAngle > 155f) || (rightLegAngle > 160f && rightWaistAngle > 155f)
                        val isDown = (leftLegAngle < 60f && leftWaistAngle < 60f) || (rightLegAngle < 60f && rightWaistAngle < 60f)

                        updateReachedPointState(
                            firstAndLastPointState = isUp,
                            middlePointState = isDown
                        )

                        landmarkInfoList.value += landmarkInfo

//                        println("jaehoLee", "leftLeg = $leftLegAngle, leftWaist = $leftWaistAngle, ${isReachedFirstPoint.value}, ${isReachedMiddlePoint.value}, ${isReachedLastPoint.value}")
                    }
                }
            }
            // 데드리프트 1회 카운트
            launch(Dispatchers.Default) {
                workoutCountBase.filter { (_, name) ->
                    name == SupportedWorkout.DEAD_LIFT.value
                }.map { (landmarkInfo, _) ->
                    landmarkInfo
                }.collectLatest { landmarkInfo ->
                    landmarkInfo.run {
                        val leftLegAngle = calculateAngle(
                            pointA = leftHip.toPair(),
                            pointB = leftKnee.toPair(),
                            pointC = leftAnkle.toPair()
                        )
                        val rightLegAngle = calculateAngle(
                            pointA = rightHip.toPair(),
                            pointB = rightKnee.toPair(),
                            pointC = rightAnkle.toPair()
                        )
                        val leftWaistAngle = calculateAngle(
                            pointA = leftShoulder.toPair(),
                            pointB = leftHip.toPair(),
                            pointC = leftKnee.toPair()
                        )
                        val rightWaistAngle = calculateAngle(
                            pointA = rightShoulder.toPair(),
                            pointB = rightHip.toPair(),
                            pointC = rightKnee.toPair()
                        )

                        val isDown = (leftLegAngle < 155f && leftWaistAngle < 100f) || (rightLegAngle < 155f && rightWaistAngle < 100f)
                        val isUp = (leftLegAngle > 165f && leftWaistAngle > 160f) || (rightLegAngle > 165f && rightWaistAngle > 160f)

                        updateReachedPointState(
                            firstAndLastPointState = isDown,
                            middlePointState = isUp
                        )

                        landmarkInfoList.value += landmarkInfo

//                        println("jaehoLee", "leftLeg = $leftLegAngle, leftWaist = $leftWaistAngle, ${isReachedFirstPoint.value}, ${isReachedMiddlePoint.value}, ${isReachedLastPoint.value}")
                    }
                }
            }
            // 1회 카운트 후 판정
            launch(Dispatchers.Default) {
                combine(
                    isReachedFirstPoint,
                    isReachedMiddlePoint,
                    isReachedLastPoint
                ) { isFirstPoint, isMiddlePoint, isLastPoint ->
                    Triple(isFirstPoint, isMiddlePoint, isLastPoint)
                }.filter { (isFirstPoint, isMiddlePoint, isLastPoint) ->
                    isFirstPoint && isMiddlePoint && isLastPoint
                }.flatMapLatest { (isFirstPoint, isMiddlePoint, isLastPoint) ->
                    interpreter.map { interpreter ->
                        interpreter to workoutName.firstOrNull()
                    }.filter { (interpreter, name) ->
                        interpreter != null && name != null &&
                                isFirstPoint && isMiddlePoint && isLastPoint
                    }
                }.map { (interpreter, name) ->
                    interpreter!! to name!!
                }.filter { (_, _) ->
                    firstTimer == null && restTimer == null
                }.collectLatest { (interpreter, name) ->
                    val frameDataList = if (landmarkInfoList.value.size > 8) {
                        val currentLandmarkInfoList = landmarkInfoList.value
                        val indexList = arrayListOf(0)
                        val dividePoint = (currentLandmarkInfoList.size - 1).toFloat() / 7f

                        for (i in 1..7) {
                            indexList.add(floor(dividePoint * i.toFloat()).toInt())
                        }

                        currentLandmarkInfoList.filterIndexed { index, _ ->
                            indexList.contains(index)
                        }
                    } else {
                        landmarkInfoList.value
                    }

                    landmarkInfoList.value = listOf()

                    // frame * bodyPart * xyCount * view * floatByteSize
                    val inputBufferSize = if (name != SupportedWorkout.SQUAT.value) {
                        1 * 8 * 12 * 2 * 5 * Float.SIZE_BYTES
                    } else {
                        1 * 8 * 11 * 2 * 5 * Float.SIZE_BYTES
                    }
                    val inputBuffer = ByteBuffer.allocateDirect(inputBufferSize).apply {
                        order(ByteOrder.nativeOrder())
                    }

                    // outputSize * floatByteSize
                    val outputBufferSize = if (name != SupportedWorkout.SQUAT.value) {
                        32 * Float.SIZE_BYTES
                    } else {
                        16 * Float.SIZE_BYTES
                    }
                    val outputBuffer = ByteBuffer.allocateDirect(outputBufferSize).apply {
                        order(ByteOrder.nativeOrder())
                    }

                    getNormalizedFrameFloatArray(frameDataList, name).forEach { value ->
                        inputBuffer.putFloat(value)
                    }

                    interpreter.run(inputBuffer, outputBuffer)

                    outputBuffer.rewind()
                    val predictionList = FloatArray(outputBufferSize / Float.SIZE_BYTES)
                    outputBuffer.asFloatBuffer().get(predictionList)

                    val highestPrediction = predictionList.toList().max()
                    val selectedPredictionIndex = predictionList.toList().indexOfFirst { prediction ->
                        prediction == highestPrediction
                    }

                    /**
                     * 스쿼트
                     * 0 true 4개
                     * 1 ~ 4 true 3개
                     * 5 ~ 10 true 2개
                     * 11~14 true 1개
                     * 15 true 0개
                     *
                     * 푸시업, 데드
                     * 0이 true 5개
                     * 1~5 true 4개
                     * 6~15 true 3개
                     * 16 ~ 25  true 2개
                     * 26~30 true 1개
                     * 31 true 0개
                     */

//                    if (name != SupportedWorkout.SQUAT.value) {
//                        when (selectedPredictionIndex) {
//                            in 0..25 -> {
//                                val decision = (0..1).random()
//
//                                if (decision == 1)
//                                    _scorePerfect.value += 1
//                                else
//                                    _scoreGood.value += 1
//                            }
//                            in 26..31 -> _scoreNotGood.value += 1
//                        }
//                    } else {
//                        when (selectedPredictionIndex) {
//                            in 0..4 -> _scorePerfect.value += 1
//                            in 5..10 -> _scoreGood.value += 1
//                            in 11..15 -> _scoreNotGood.value += 1
//                        }
//                    }

                    if (name != SupportedWorkout.SQUAT.value) {
                        when (selectedPredictionIndex) {
                            in 0..15 -> _scorePerfect.value += 1
                            in 16..25 -> _scoreGood.value += 1
                            in 26..31 -> _scoreNotGood.value += 1
                        }
                    } else {
                        when (selectedPredictionIndex) {
                            in 0..4 -> _scorePerfect.value += 1
                            in 5..10 -> _scoreGood.value += 1
                            in 11..15 -> _scoreNotGood.value += 1
                        }
                    }

                    if (name != SupportedWorkout.SQUAT.value) {
                        if (selectedPredictionIndex in 0..31) {
                            workoutResultIndexList.value[currentSet.value - 1].add(selectedPredictionIndex)
                        }
                    } else {
                        if (selectedPredictionIndex in 0..15) {
                            workoutResultIndexList.value[currentSet.value - 1].add(selectedPredictionIndex)
                        }
                    }

                    workoutInfo.value = workoutInfo.value?.run {
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

                    inputBuffer.clear()
                    outputBuffer.clear()

                    isReachedFirstPoint.value = false
                    isReachedMiddlePoint.value = false
                    isReachedLastPoint.value = false
                }
            }
            launch(Dispatchers.Default) {
                combine(
                    isSetFinished,
                    isWorkoutFinished
                ) { isSetFinished, isWorkoutFinished ->
                    isSetFinished to isWorkoutFinished
                }.filter { (isSetFinished, isWorkoutFinished) ->
                    val set = currentSet.value
                    val maxSet = currentMaxSet.firstOrNull()

                    (isSetFinished || isWorkoutFinished) &&
                            !(isSetFinished && !isWorkoutFinished && set == maxSet)
                }.collectLatest { (isSetFinished, isWorkoutFinished) ->
                    if (!isWorkoutFinished) {
                        if (isSetFinished) {
                            stopWorkoutTimer()
                            startRestCountdownTimer()
                        }
                    } else {
                        stopWorkoutTimer()

                        if (MainApplication.appPreference.isLogin) {
                            val req = workoutInfo.value?.run {
                                PostWorkoutDataReq(
                                    noteId = noteId.value,
                                    exerciseName = workoutName,
                                    workoutList = setDataList.filter { setData ->
                                        setData.run {
                                            if (workoutName != SupportedWorkout.PUSH_UP.value) {
                                                weight != null && repeats != null
                                            } else {
                                                repeats != null
                                            }
                                        }
                                    }.distinctBy { setData ->
                                        setData.setNum
                                    }.sortedBy { setData ->
                                        setData.setNum
                                    }
                                )
                            }

                            if (MainApplication.appPreference.isLogin) {
                                _workoutUiEvent.emit(WorkoutUiEvent.StopRecording)
                            } else {
                                _workoutUiEvent.emit(WorkoutUiEvent.StopFakeRecording)
                            }

                            req?.run {
                                requestPostWorkoutData(this)
                            } ?: println("jaehoLee", "PostWorkoutData is null.")
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
            workoutResultIndexList.value = arrayListOf()

            viewModelScope.launch {
                delay(2000L)
                if (MainApplication.appPreference.isLogin) {
                    _workoutUiEvent.emit(WorkoutUiEvent.StopRecording)
                } else {
                    _workoutUiEvent.emit(WorkoutUiEvent.StopFakeRecording)
                }
            }
        }
    }

    private fun clearWorkoutData(onFinishClear: () -> Unit = { }) {
        workoutInfo.value = null
        _currentSet.value = 0
        _isWorkoutRunning.value = false
        landmarkInfoList.value = listOf()
        _scorePerfect.value = 0
        _scoreGood.value = 0
        _scoreNotGood.value = 0
        isFinishedRequestPostWorkoutData.value = false

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

        interpreter.value?.close()
        interpreter.value = null

        onFinishClear()
    }

    fun setWorkoutInfo(info: WorkoutInfo) {
        workoutInfo.value = info.copy(
            setDataList = info.setDataList.map { workoutData ->
                workoutData.copy(
                    weight = if (info.workoutName != SupportedWorkout.PUSH_UP.value) {
                        workoutData.weight
                    } else {
                        null
                    }
                )
            }
        )
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

    private fun startWorkoutTimer() {
        workoutTimer = timer(period = 500L) {
            currentWorkoutTime.value += 500L
        }

        _currentSet.value += 1
        workoutResultIndexList.value.add(arrayListOf())
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
        pose: Pose
    ) {
        viewModelScope.launch {
//            if (!isLoadingFinishWorkout.value) {
//                updatedPose.emit(pose)
//            }
            updatedPose.emit(pose)

            if (poseList.size == 1 && !isLoadingFinishWorkout.value) {
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
        val currentPoseList = poseList.toList()
        val processedPoseList = currentPoseList.map { savedPose ->
            savedPose.allPoseLandmarks.filter { landmark ->
                landmark.landmarkType >= PoseLandmark.LEFT_SHOULDER
            }.sortedBy { landmark ->
                landmark.landmarkType
            }.map { landmark ->
                landmark.position.run { x to y }
            }
        }

        return if (processedPoseList.isNotEmpty()) {
            processedPoseList.firstOrNull()?.mapIndexed { index, (x, y) ->
                var resultX = x
                var resultY = y

                for (i in 1 until processedPoseList.size) {
                    val (nextX, nextY) = processedPoseList[i][index]

                    resultX += nextX
                    resultY += nextY
                }

                resultX / processedPoseList.size.toFloat() to resultY / processedPoseList.size.toFloat()
            } ?: listOf()
        } else {
            listOf()
        }
    }
    private fun updateBodyFrameData(
        cameraWidthPx: Int,
        cameraHeightPx: Int,
        imageWidth: Int,
        imageHeight: Int,
        landmarkPositionList: List<Pair<Float, Float>>
    ) {
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

    private fun updateReachedPointState(
        firstAndLastPointState: Boolean,
        middlePointState: Boolean
    ) {
        if (!isReachedFirstPoint.value) {
            isReachedFirstPoint.value = firstAndLastPointState
        }

        if (!isReachedMiddlePoint.value) {
            isReachedMiddlePoint.value =
                isReachedFirstPoint.value && middlePointState
        }

        if (!isReachedLastPoint.value) {
            isReachedLastPoint.value =
                isReachedFirstPoint.value && isReachedMiddlePoint.value &&
                        firstAndLastPointState
        }
    }

    private suspend fun requestGetExercises() {
        makeRequest {
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
        makeRequest {
            workoutRepository.postWorkoutNote(MainApplication.appPreference.userId)
        }.onSuccess { res ->
            println("jaehoLee", "noteId Before = ${noteId.value}")
            noteId.value = res.result?.noteId ?: -1
            println("jaehoLee", "noteId After = ${noteId.value}")

            startFirstCountdownTimer()
        }.onFailure { res ->
            println("jaehoLee", "onFailure of postWorkoutNote(): (${res.code}), ${res.message}")
//            startFirstCountdownTimer()
        }.onError { throwable ->
            println("jaehoLee", "onError of postWorkoutNote(): ${throwable.message}")
//            startFirstCountdownTimer()
        }
    }

    private suspend fun requestPostWorkoutData(req: PostWorkoutDataReq) {
        makeRequest {
            workoutRepository.postWorkoutData(req)
        }.onSuccess {
            workoutNameForVideo.value = workoutName.firstOrNull() ?: ""
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
        var videoFileDir = MainApplication.appPreference.currentVideoFileDir

        while (videoFileDir == null) {
            videoFileDir = MainApplication.appPreference.currentVideoFileDir
        }

        _isLoadingFinishWorkout.value = true

        val workoutResultIndexListString = workoutResultIndexList.value
            .filter { resultList -> resultList.isNotEmpty() }
            .toString()
            .replace("[[", "[")
            .replace("]]", "]")
            .replace("[", "")
            .replace("]", "/")
            .replace(" ", "")

        makeRequest {
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
                    workoutResultIndexList.value = arrayListOf()

                    viewModelScope.launch {
                        _workoutUiEvent.emit(
                            WorkoutUiEvent.FinishWorkout(
                                noteId,
                                workoutName,
                                workoutResultIndexListString
                            )
                        )
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
                    workoutResultIndexList.value = arrayListOf()

                    viewModelScope.launch {
                        _workoutUiEvent.emit(
                            WorkoutUiEvent.FinishWorkout(
                                noteId,
                                workoutName,
                                workoutResultIndexListString
                            )
                        )
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
                    workoutResultIndexList.value = arrayListOf()

                    viewModelScope.launch {
                        _workoutUiEvent.emit(
                            WorkoutUiEvent.FinishWorkout(
                                noteId,
                                workoutName,
                                workoutResultIndexListString
                            )
                        )
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
        data object StartFakeRecording : WorkoutUiEvent()
        data object StopFakeRecording : WorkoutUiEvent()
        data class FinishWorkout(
            val noteId: Int,
            val workoutName: String,
            val workoutResultIndexListString: String
        ) : WorkoutUiEvent()
    }
}