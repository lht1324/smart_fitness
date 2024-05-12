package com.overeasy.smartfitness.domain.diet.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.diet.model.DietRecommendResult
import kotlinx.serialization.Serializable

@Serializable
data class GetDietRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean = false,

    val result: DietRecommendResult,
) : BaseResponseModel
