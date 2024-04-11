package com.overeasy.smartfitness.domain.diet.entity

import com.overeasy.smartfitness.domain.diet.model.CategoryItem
import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.Serializable

@Serializable
data class GetDietCategoryRes(
    override val code: Int,
    override val msg: String,
    val categoryList: List<CategoryItem>
) : BaseResponseModel
