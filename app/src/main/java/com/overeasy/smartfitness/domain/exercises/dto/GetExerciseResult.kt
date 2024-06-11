package com.overeasy.smartfitness.domain.exercises.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetExerciseResult(
    val exerciseList: List<ExerciseData>
)
