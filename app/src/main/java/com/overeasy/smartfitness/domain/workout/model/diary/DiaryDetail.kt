package com.overeasy.smartfitness.domain.workout.model.diary

data class DiaryDetail(
    val totalKcal: Int,
    val totalPerfect: Int,
    val totalGood: Int,
    val totalBad: Int,
    val totalScore: Int,
    val workoutList: List<DiaryDetailWorkoutInfo>
)
