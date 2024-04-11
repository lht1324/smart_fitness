package com.overeasy.smartfitness.domain.workout.model

import kotlinx.serialization.Serializable

@Serializable
data class Score(
    val name: String,
    val score: Int
)
