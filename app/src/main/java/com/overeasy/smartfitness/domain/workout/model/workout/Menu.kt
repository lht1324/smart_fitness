package com.overeasy.smartfitness.domain.workout.model.workout

import kotlinx.serialization.Serializable

@Serializable
data class Menu(
    val name: String,
    val nutritionAmountList: List<NutritionAmount>
)
