package com.overeasy.smartfitness.domain.diary.model

import kotlinx.serialization.Serializable

@Serializable
data class Workout(
    val name: String,
    val setCountList: List<SetCount>,
    val calorieUsage: Int
)
