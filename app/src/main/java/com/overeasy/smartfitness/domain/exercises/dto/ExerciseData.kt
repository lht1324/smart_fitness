package com.overeasy.smartfitness.domain.exercises.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseData(
    val exerciseId: Int,
    val exerciseName: String,
    val perKcal: Int,
    val exerciseType: String
)
