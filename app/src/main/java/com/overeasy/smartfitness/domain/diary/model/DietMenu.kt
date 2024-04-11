package com.overeasy.smartfitness.domain.diary.model

import kotlinx.serialization.Serializable

@Serializable
data class DietMenu(
    val name: String,
    val calorie: Int
)
