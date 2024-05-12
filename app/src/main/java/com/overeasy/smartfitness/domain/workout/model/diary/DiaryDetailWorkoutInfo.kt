package com.overeasy.smartfitness.domain.workout.model.diary

import kotlinx.serialization.Serializable

@Serializable
data class DiaryDetailWorkoutInfo(
    val workoutId: Int,
    val noteId: Int,
    val exerciseName: String,
    val setNum: Int,
    val repeats: Int,
    val weight: Int
)