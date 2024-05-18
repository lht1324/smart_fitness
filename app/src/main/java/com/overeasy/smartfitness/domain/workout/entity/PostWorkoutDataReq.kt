package com.overeasy.smartfitness.domain.workout.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.workout.model.workout.WorkoutData
import com.overeasy.smartfitness.domain.workout.model.workout.WorkoutDataResult
import kotlinx.serialization.Serializable

@Serializable
data class PostWorkoutDataReq(
    val noteId: Int,
    val exerciseName: String,
    val workoutList: List<WorkoutData>
)
