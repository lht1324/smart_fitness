package com.overeasy.smartfitness.domain.workout.model.workout

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutVideoList(
    val workoutVideoList: List<WorkoutVideoData>
)
