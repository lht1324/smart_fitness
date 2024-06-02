package com.overeasy.smartfitness.domain.ai.model

import com.overeasy.smartfitness.domain.workout.model.workout.LandmarkCoordinate
import kotlinx.serialization.Serializable

@Serializable
data class LandmarkInfo(
    val workoutName: String,

    val nose: LandmarkCoordinate,

    val leftShoulder: LandmarkCoordinate,
    val rightShoulder: LandmarkCoordinate,
    val leftElbow: LandmarkCoordinate,
    val rightElbow: LandmarkCoordinate,
    val leftWrist: LandmarkCoordinate,
    val rightWrist: LandmarkCoordinate,
    val leftHip: LandmarkCoordinate,
    val rightHip: LandmarkCoordinate,
    val leftKnee: LandmarkCoordinate,
    val rightKnee: LandmarkCoordinate,
    val leftAnkle: LandmarkCoordinate,
    val rightAnkle: LandmarkCoordinate,
    val leftPinky: LandmarkCoordinate,
    val rightPinky: LandmarkCoordinate,
    val leftIndex: LandmarkCoordinate,
    val rightIndex: LandmarkCoordinate,
    val leftThumb: LandmarkCoordinate,
    val rightThumb: LandmarkCoordinate,
    val leftHeel: LandmarkCoordinate,
    val rightHeel: LandmarkCoordinate,
    val leftFootIndex: LandmarkCoordinate,
    val rightFootIndex: LandmarkCoordinate
)
