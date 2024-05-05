package com.overeasy.smartfitness.scenario.workout.workout

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.entity.PostWorkoutReq
import com.overeasy.smartfitness.domain.workout.model.LandmarkCoordinate
import com.overeasy.smartfitness.model.workout.BodyFrameData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    private val _workoutUiEvent = MutableSharedFlow<WorkoutUiEvent>()
    val workoutUiEvent = _workoutUiEvent.asSharedFlow()

    private val _bodyFrameData = MutableStateFlow<BodyFrameData?>(null)
    val bodyFrameData = _bodyFrameData.asStateFlow()

    private val poseList = mutableStateListOf<Pose>()

    fun onUpdatePose(
        cameraWidthPx: Int,
        cameraHeightPx: Int,
        imageWidth: Int = 480,
        imageHeight: Int = 640,
        pose: Pose,
        copy: (String) -> Unit
    ) {
        // 3개 평균치 내서 한 번에 하기
        if (poseList.size == 3) {
            val processList = poseList.map { savedPose ->
                savedPose.allPoseLandmarks.filter { landmark ->
                    landmark.landmarkType >= PoseLandmark.LEFT_SHOULDER
                }.sortedBy { landmark ->
                    landmark.landmarkType
                }.map { landmark ->
                    landmark.position.run { x to y }
                }
            }
            val averageLandmarkPositionList = processList[0].mapIndexed { index, (x, y) ->
                var resultX = x
                var resultY = y

                for (i in 1 until processList.size) {
                    val (nextX, nextY) = processList[i][index]

                    resultX += nextX
                    resultY += nextY
                }

                resultX / processList.size.toFloat() to resultY / processList.size.toFloat()
            }
            updateBodyFrameData(
                cameraWidthPx = cameraWidthPx,
                cameraHeightPx = cameraHeightPx,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                landmarkPositionList = averageLandmarkPositionList,
                copy = copy
            )
            poseList.clear()
        }

        poseList.add(pose)
    }
    private fun updateBodyFrameData(
        cameraWidthPx: Int,
        cameraHeightPx: Int,
        imageWidth: Int,
        imageHeight: Int,
        landmarkPositionList: List<Pair<Float, Float>>,
        copy: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
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
                val widthRatio = (x - invisibleImageWidthHalf) / (imageWidthFloat - invisibleImageWidth)
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
            // 저장용
//            launch(Dispatchers.IO) {
//                saveData(pose, copy)
//            }
            _bodyFrameData.value = data
        }
    }

    private fun LandmarkCoordinate.matchResolution(landmark: Int): LandmarkCoordinate {
        /**
         * 480 x 640
         * to
         * 1920 x 1080
         * 1. 640 / 1080 = ratio
         * 2. 1920 * ratio = smallWidth
         * 3. (smallWidth - 480) / 2f = leftSide
         * 4. bigRatio = 1080 / 640
         * (x + leftSide) * bigRatio
         * y * bigRatio
         */
        val ratio = 640f / 1080f
        val smallWidth = 1920f * ratio
        val smallWidthLeftSide = (smallWidth - 480f) / 2f
        val bigRatio = 1080f / 640f

        return copy(
            x = (x + smallWidthLeftSide) * bigRatio,
            y = y * bigRatio
        )
    }

    private suspend fun saveData(pose: Pose, copy: (String) -> Unit) {
        if (pose.allPoseLandmarks.size != PoseLandmark.RIGHT_FOOT_INDEX) {
            val req = pose.run {
                PostWorkoutReq(
                    workoutName = "push_up",

                    leftShoulder = getPoseLandmark(PoseLandmark.LEFT_SHOULDER)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.LEFT_SHOULDER)
                    },
                    rightShoulder = getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.RIGHT_SHOULDER)
                    },
                    leftElbow = getPoseLandmark(PoseLandmark.LEFT_ELBOW)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.LEFT_ELBOW)
                    },
                    rightElbow = getPoseLandmark(PoseLandmark.RIGHT_ELBOW)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.RIGHT_ELBOW)
                    },
                    leftWrist = getPoseLandmark(PoseLandmark.LEFT_WRIST)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.LEFT_WRIST)
                    },
                    rightWrist = getPoseLandmark(PoseLandmark.RIGHT_WRIST)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.RIGHT_WRIST)
                    },
                    leftHip = getPoseLandmark(PoseLandmark.LEFT_HIP)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.LEFT_HIP)
                    },
                    rightHip = getPoseLandmark(PoseLandmark.RIGHT_HIP)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.RIGHT_HIP)
                    },
                    leftKnee = getPoseLandmark(PoseLandmark.LEFT_KNEE)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.LEFT_KNEE)
                    },
                    rightKnee = getPoseLandmark(PoseLandmark.RIGHT_KNEE)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.RIGHT_KNEE)
                    },
                    leftAnkle = getPoseLandmark(PoseLandmark.LEFT_ANKLE)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.LEFT_ANKLE)
                    },
                    rightAnkle = getPoseLandmark(PoseLandmark.RIGHT_ANKLE)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.RIGHT_ANKLE)
                    },
                    leftPinky = getPoseLandmark(PoseLandmark.LEFT_PINKY)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.LEFT_PINKY)
                    },
                    rightPinky = getPoseLandmark(PoseLandmark.RIGHT_PINKY)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.RIGHT_PINKY)
                    },
                    leftIndex = getPoseLandmark(PoseLandmark.LEFT_INDEX)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.LEFT_INDEX)
                    },
                    rightIndex = getPoseLandmark(PoseLandmark.RIGHT_INDEX)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.RIGHT_INDEX)
                    },
                    leftThumb = getPoseLandmark(PoseLandmark.LEFT_THUMB)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.LEFT_THUMB)
                    },
                    rightThumb = getPoseLandmark(PoseLandmark.RIGHT_THUMB)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.RIGHT_THUMB)
                    },
                    leftHeel = getPoseLandmark(PoseLandmark.LEFT_HEEL)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.LEFT_HEEL)
                    },
                    rightHeel = getPoseLandmark(PoseLandmark.RIGHT_HEEL)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.RIGHT_HEEL)
                    },
                    leftFootIndex = getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.LEFT_FOOT_INDEX)
                    },
                    rightFootIndex = getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)!!.position.run {
                        LandmarkCoordinate(
                            x = x,
                            y = y
                        ).matchResolution(PoseLandmark.RIGHT_FOOT_INDEX)
                    }
                )
            }
            val json = Gson().toJson(req)

//            tempList.add(json)
            copy(json)
//            val file = File("/data/user/0/com.overeasy.smartfitness.debug/files", time)
//            file.writeText(json)
        }
    }

    sealed class WorkoutUiEvent {
        data class CopyText(val text: String) : WorkoutUiEvent()
    }
}