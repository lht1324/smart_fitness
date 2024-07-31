package com.overeasy.smartfitness.domain.workout.entity

data class DiaryDetailWorkoutInfo(
    val noteId: Int,
    val workoutName: String,
    val setCount: Int,
    val repeatCount: Int,
    val weight: Int,
    val caloriePerEachCount: Int = 0
)
