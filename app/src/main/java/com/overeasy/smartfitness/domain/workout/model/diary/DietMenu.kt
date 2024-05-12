package com.overeasy.smartfitness.domain.workout.model.diary

import kotlinx.serialization.Serializable

@Serializable
data class DietMenu(
    val name: String,
    val calorie: Int
)
