package com.overeasy.smartfitness.domain.workout.model

import kotlinx.serialization.Serializable

@Serializable
data class Menu(
    val name: String,
    val nutritionAmountList: List<NutritionAmount>
)
