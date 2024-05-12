package com.overeasy.smartfitness.domain.ranking.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.ranking.model.RankingCategory
import kotlinx.serialization.Serializable

@Serializable
data class GetRankingCategoryRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean,

    val result: RankingCategory,
) : BaseResponseModel