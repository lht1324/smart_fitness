package com.overeasy.smartfitness.domain.diet.model

import com.overeasy.smartfitness.R
import com.overeasy.smartfitness.scenario.diet.diet.FoodCategory
import kotlinx.serialization.Serializable

@Serializable
data class CategoryItem(
    val foodCategory: FoodCategory
) {
    val drawableId = when (foodCategory) {
        FoodCategory.KOREAN -> R.drawable.food_category_korean
        FoodCategory.CHINESE -> R.drawable.food_category_chinese
        FoodCategory.JAPANESE -> R.drawable.food_category_japansese
        FoodCategory.WESTERN -> R.drawable.food_category_western
        FoodCategory.FASTFOOD -> R.drawable.food_category_fastfood
        FoodCategory.DESSERT -> R.drawable.food_category_dessert
    }
}