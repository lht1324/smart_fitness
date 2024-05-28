package com.overeasy.smartfitness.domain.diet.dto

import com.overeasy.smartfitness.domain.diet.model.RecommendedFood
import kotlinx.serialization.Serializable

@Serializable
data class FoodRecommend(
    val code: String,
    val name: String,
    val mainFoodType: String,
    val calorie: Float,
    val carbohydrate: Float,
    val protein: Float,
    val fat: Float,
    val similarity_score: Float
)

fun FoodRecommend.toEntity() = run {
    RecommendedFood(
        name = name,
        foodType = mainFoodType,
        calorie = calorie,
        carbohydrate = carbohydrate,
        protein = protein,
        fat = fat,
        similarityScore = similarity_score
    )
}