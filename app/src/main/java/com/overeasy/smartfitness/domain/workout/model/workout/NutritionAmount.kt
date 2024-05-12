package com.overeasy.smartfitness.domain.workout.model.workout

import kotlinx.serialization.Serializable

@Serializable
data class NutritionAmount(
    val name: String,
    val amount: Int
)
