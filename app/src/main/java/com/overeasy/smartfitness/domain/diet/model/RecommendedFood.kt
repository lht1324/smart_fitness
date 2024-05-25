package com.overeasy.smartfitness.domain.diet.model

import kotlinx.serialization.Serializable

@Serializable
data class RecommendedFood(
    val code: String,
    val name: String,
    val mainFoodType: String,
    val calorie: Float,
    val similarity_score: Float
)
