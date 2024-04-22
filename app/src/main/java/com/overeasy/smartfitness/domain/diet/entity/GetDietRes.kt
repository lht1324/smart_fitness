package com.overeasy.smartfitness.domain.diet.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.diet.model.DietRecommendResult
import kotlinx.serialization.Serializable

@Serializable
data class GetDietRes(
    override val code: Int = -1,
    override val message: String,
    val result: DietRecommendResult,

    override val success: Boolean = false,
    override val error: String? = null,
    override val timestamp: String? = null,
    override val path: String? = null
) : BaseResponseModel
