package com.overeasy.smartfitness.domain.diet.model

import kotlinx.serialization.Serializable

@Serializable
data class DietHistory(
    val foodName: String,
    val totalCalories: Float
)
