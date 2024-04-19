package com.overeasy.smartfitness.domain.diet.entity

import com.overeasy.smartfitness.domain.diet.model.CategoryItem
import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.Serializable

@Serializable
data class GetDietCategoryRes(
    override val status: Int,
    override val message: String,
    val categoryList: List<CategoryItem>,

    override val success: Boolean = false,
    override val error: String? = null,
    override val timestamp: String? = null,
    override val path: String? = null
) : BaseResponseModel
