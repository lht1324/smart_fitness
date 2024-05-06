package com.overeasy.smartfitness.domain.ranking.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.ranking.model.RankingInfo
import com.overeasy.smartfitness.domain.ranking.model.RankingInfoResult
import kotlinx.serialization.Serializable

@Serializable
data class GetRankingRes(
    override val code: Int = -1,
    override val message: String,
    val result: RankingInfoResult? = null,

    override val success: Boolean = false,
    override val error: String? = null,
    override val timestamp: String? = null,
    override val path: String? = null
) : BaseResponseModel