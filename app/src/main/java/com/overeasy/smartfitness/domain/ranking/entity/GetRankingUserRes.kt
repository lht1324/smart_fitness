package com.overeasy.smartfitness.domain.ranking.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.ranking.model.RankingUserInfo
import kotlinx.serialization.Serializable

@Serializable
data class GetRankingUserRes(
    override val code: Int = -1,
    override val message: String,
    val result: RankingUserInfo? = null,

    override val success: Boolean = false,
    override val error: String? = null,
    override val timestamp: String? = null,
    override val path: String? = null
) : BaseResponseModel
