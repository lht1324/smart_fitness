package com.overeasy.smartfitness.domain.workout.model.workout

import kotlinx.serialization.Serializable

@Serializable
data class Score(
    val name: String,
    val score: Int
)
