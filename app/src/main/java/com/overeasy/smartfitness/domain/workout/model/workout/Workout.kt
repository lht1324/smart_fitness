package com.overeasy.smartfitness.domain.workout.model.workout

import kotlinx.serialization.Serializable

@Serializable
data class Workout(
    val name: String,
    val setCountList: List<SetCount>,
    val calorieUsage: Int
)
