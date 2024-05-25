package com.overeasy.smartfitness.domain.workout.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.workout.model.diary.DiaryDetailResult
import com.overeasy.smartfitness.domain.workout.model.diary.DiaryDetailWorkoutInfo
import kotlinx.serialization.Serializable

@Serializable
data class GetDiaryDetailRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean,

    val result: DiaryDetailResult
) : BaseResponseModel
