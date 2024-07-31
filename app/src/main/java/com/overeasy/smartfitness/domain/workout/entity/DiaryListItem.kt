package com.overeasy.smartfitness.domain.workout.entity

data class DiaryListItem(
    val noteId: Int,
    val perfectCount: Int,
    val goodCount: Int,
    val notGoodCount: Int,
    val totalScore: Int,
    val totalKcal: Int?,
)