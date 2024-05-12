package com.overeasy.smartfitness.domain.workout.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.workout.model.diary.DiaryResult
import com.overeasy.smartfitness.domain.workout.model.workout.WorkoutNoteResult
import kotlinx.serialization.Serializable

@Serializable
data class PostWorkoutNoteRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean,

    val result: WorkoutNoteResult?,
) : BaseResponseModel