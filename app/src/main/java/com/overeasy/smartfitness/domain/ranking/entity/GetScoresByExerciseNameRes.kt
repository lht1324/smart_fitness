package com.overeasy.smartfitness.domain.ranking.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.ranking.model.RankingInfoResult
import kotlinx.serialization.Serializable

@Serializable
data class GetScoresByExerciseNameRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean,

    val result: RankingInfoResult? = null,
) : BaseResponseModel