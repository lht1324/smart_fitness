package com.overeasy.smartfitness.domain.workout.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel

data class GetWorkoutNoteRes(
    override val code: Int,
    override val message: String,
    override val success: Boolean,

//    val workoutResult: WorkoutResult,
) : BaseResponseModel
