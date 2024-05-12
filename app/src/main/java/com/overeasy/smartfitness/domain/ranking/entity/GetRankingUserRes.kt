package com.overeasy.smartfitness.domain.ranking.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.ranking.model.RankingUserInfo
import kotlinx.serialization.Serializable

@Serializable
data class GetRankingUserRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean = false,

    val result: RankingUserInfo? = null
) : BaseResponseModel
