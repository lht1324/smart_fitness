package com.overeasy.smartfitness.domain.foods.dto

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.Serializable

@Serializable
data class GetFoodsInitRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean,

    val result: List<MenuData>
) : BaseResponseModel
