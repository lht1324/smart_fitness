package com.overeasy.smartfitness.domain.diet.entity

import kotlinx.serialization.Serializable

@Serializable
data class RecommendedFood(
    val name: String,
    val foodType: String,
    val calorie: Float,
    val carbohydrate: Float,
    val protein: Float,
    val fat: Float,
    val similarityScore: Float
)
