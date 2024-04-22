package com.overeasy.smartfitness.domain.workout.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.workout.model.WorkoutResult
import kotlinx.serialization.Serializable

@Serializable
data class GetWorkoutResultRes(
    override val code: Int,
    override val message: String,
    val workoutResult: WorkoutResult,

    override val success: Boolean = false,
    override val error: String? = null,
    override val timestamp: String? = null,
    override val path: String? = null
) : BaseResponseModel
