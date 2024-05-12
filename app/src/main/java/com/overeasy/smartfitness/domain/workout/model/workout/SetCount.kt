package com.overeasy.smartfitness.domain.workout.model.workout

import kotlinx.serialization.Serializable

@Serializable
data class SetCount(
    val set: Int,
    val count: Int
)
