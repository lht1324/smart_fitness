package com.overeasy.smartfitness.domain.workout.entity

import com.overeasy.smartfitness.domain.workout.model.workout.WorkoutData
import kotlinx.serialization.Serializable

@Serializable
data class PostWorkoutDataReq(
    val noteId: Int,
    val exerciseName: String,
    val workoutList: List<WorkoutData>
)
