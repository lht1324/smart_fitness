package com.overeasy.smartfitness.domain.workout.model

import kotlinx.serialization.Serializable

@Serializable
data class NutritionAmount(
    val name: String,
    val amount: Int
)
