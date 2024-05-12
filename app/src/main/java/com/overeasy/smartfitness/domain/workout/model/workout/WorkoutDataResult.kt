package com.overeasy.smartfitness.domain.workout.model.workout

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutDataResult(
    val noteId: Int,
    val exerciseName: String,
    val workoutList: List<WorkoutData>
)
