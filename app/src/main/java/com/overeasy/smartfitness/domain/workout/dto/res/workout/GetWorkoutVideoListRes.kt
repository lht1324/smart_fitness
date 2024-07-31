package com.overeasy.smartfitness.domain.workout.dto.res.workout

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.Serializable

@Serializable
data class GetWorkoutVideoListRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean,

    val result: GetWorkoutVideoListResult,
) : BaseResponseModel