package com.overeasy.smartfitness.domain.diet.dto

import kotlinx.serialization.Serializable

@Serializable
data class DietHistory(
    val foodName: String,
    val totalCalories: Float
)
