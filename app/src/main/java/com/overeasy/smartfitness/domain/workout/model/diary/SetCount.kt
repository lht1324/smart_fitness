package com.overeasy.smartfitness.domain.workout.model.diary

import kotlinx.serialization.Serializable

@Serializable
data class SetCount(
    val set: Int,
    val count: Int
)
