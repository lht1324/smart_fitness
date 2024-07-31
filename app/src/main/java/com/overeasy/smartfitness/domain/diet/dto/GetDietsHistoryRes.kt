package com.overeasy.smartfitness.domain.diet.dto

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.Serializable

@Serializable
data class GetDietsHistoryRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean = false,

    val result: DietHistoryResult,
) : BaseResponseModel