package com.overeasy.smartfitness.domain.diet.model

import com.overeasy.smartfitness.R
import kotlinx.serialization.Serializable

@Serializable
data class CategoryItem(
    val name: String
) {
    val drawableId = when (name) {
        "한식" -> R.drawable.food_category_korean
        "중식" -> R.drawable.food_category_chinese
        "일식" -> R.drawable.food_category_japansese
        "양식" -> R.drawable.food_category_western
        "패스트푸드" -> R.drawable.food_category_fastfood
        "디저트" -> R.drawable.food_category_dessert
        else -> R.drawable.food_category_korean
    }
}