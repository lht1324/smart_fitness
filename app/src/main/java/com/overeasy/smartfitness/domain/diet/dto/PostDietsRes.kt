package com.overeasy.smartfitness.domain.diet.dto

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.diet.dto.DietRecommendResult
import kotlinx.serialization.Serializable

@Serializable
data class PostDietsRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean = false,

    val result: DietRecommendResult,
) : BaseResponseModel
