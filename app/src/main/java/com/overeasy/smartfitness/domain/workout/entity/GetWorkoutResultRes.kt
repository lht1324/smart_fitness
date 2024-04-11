package com.overeasy.smartfitness.domain.workout.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.workout.model.WorkoutResult
import kotlinx.serialization.Serializable

@Serializable
data class GetWorkoutResultRes(
    override val code: Int,
    override val msg: String,
    val workoutResult: WorkoutResult
) : BaseResponseModel
