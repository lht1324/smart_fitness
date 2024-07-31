package com.overeasy.smartfitness.domain.workout.dto.res.note

import com.overeasy.smartfitness.domain.workout.entity.DiaryListItem
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val noteId: Int,
    val userId: Int,
    val workoutDate: List<Int>,
    val totalScore: Int,
    val totalKcal: Int?,
    val totalPerfect: Int,
    val totalGood: Int,
    val totalBad: Int
)

fun Note.toEntity() = run {
    DiaryListItem(
        noteId = noteId,
        totalScore = totalScore,
        totalKcal = totalKcal,
        perfectCount = totalPerfect,
        goodCount = totalGood,
        notGoodCount = totalBad,
    )
}