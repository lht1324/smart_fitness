package com.overeasy.smartfitness.domain.diet.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.diet.model.DietHistoryResult
import kotlinx.serialization.Serializable

@Serializable
data class GetDietsHistoryRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean = false,

    val result: DietHistoryResult,
) : BaseResponseModel
