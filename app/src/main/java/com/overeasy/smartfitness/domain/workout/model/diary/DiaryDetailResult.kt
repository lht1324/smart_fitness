package com.overeasy.smartfitness.domain.workout.model.diary

import kotlinx.serialization.Serializable

@Serializable
data class DiaryDetailResult(
    val totalKcal: Int = 0,
    val totalPerfect: Int = 0,
    val totalGood: Int = 0,
    val totalBad: Int = 0,
    val totalScore: Int = 0,
    val workoutList: List<DiaryDetailWorkoutInfo> = listOf()
)