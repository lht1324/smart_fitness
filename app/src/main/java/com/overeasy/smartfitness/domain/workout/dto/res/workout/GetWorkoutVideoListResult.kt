package com.overeasy.smartfitness.domain.workout.dto.res.workout

import kotlinx.serialization.Serializable

@Serializable
data class GetWorkoutVideoListResult(
    val workoutVideoList: List<WorkoutVideoData>
)
