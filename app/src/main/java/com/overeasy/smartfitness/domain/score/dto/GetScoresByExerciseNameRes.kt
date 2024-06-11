package com.overeasy.smartfitness.domain.score.dto

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.Serializable

@Serializable
data class GetScoresByExerciseNameRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean,

    val result: RankingInfoResult? = null,
) : BaseResponseModel