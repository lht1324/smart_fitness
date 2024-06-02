package com.overeasy.smartfitness.domain.workout.dto.res.workout

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutVideoList(
    val workoutVideoList: List<WorkoutVideoData>
)
