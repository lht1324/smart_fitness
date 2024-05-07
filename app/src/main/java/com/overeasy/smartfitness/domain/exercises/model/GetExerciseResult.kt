package com.overeasy.smartfitness.domain.exercises.model

import kotlinx.serialization.Serializable

@Serializable
data class GetExerciseResult(
    val exerciseList: List<ExerciseData>
)
