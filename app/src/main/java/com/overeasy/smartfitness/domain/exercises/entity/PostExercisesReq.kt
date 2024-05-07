package com.overeasy.smartfitness.domain.exercises.entity

import kotlinx.serialization.Serializable

@Serializable
data class PostExercisesReq(
    val exerciseName: String,
    val exerciseType: String,
    val perKcal: Int
)
