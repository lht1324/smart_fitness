package com.overeasy.smartfitness.domain.workout.dto.req

import kotlinx.serialization.Serializable

@Serializable
data class PostWorkoutDataReq(
    val noteId: Int,
    val exerciseName: String,
    val workoutList: List<WorkoutData>
)
