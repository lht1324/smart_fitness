package com.overeasy.smartfitness.domain.workout.entity

import com.overeasy.smartfitness.domain.workout.entity.DiaryDetailWorkoutInfo

data class DiaryDetail(
    val perfectCount: Int,
    val goodCount: Int,
    val notGoodCount: Int,
    val totalScore: Int,
    val totalKcal: Int,
    val workoutInfoList: List<DiaryDetailWorkoutInfo>
)
