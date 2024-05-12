package com.overeasy.smartfitness.domain.workout.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.workout.model.diary.DiaryResult
import kotlinx.serialization.Serializable

@Serializable
data class GetDiaryRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean,

    val result: DiaryResult?,
) : BaseResponseModel