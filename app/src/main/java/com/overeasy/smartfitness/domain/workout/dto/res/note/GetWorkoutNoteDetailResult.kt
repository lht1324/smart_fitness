package com.overeasy.smartfitness.domain.workout.dto.res.note

import com.overeasy.smartfitness.domain.workout.entity.DiaryDetail
import kotlinx.serialization.Serializable

@Serializable
data class GetWorkoutNoteDetailResult(
    val totalKcal: Int = 0,
    val totalPerfect: Int = 0,
    val totalGood: Int = 0,
    val totalBad: Int = 0,
    val totalScore: Int = 0,
    val workoutList: List<NoteDetailWorkoutInfo> = listOf()
)

fun GetWorkoutNoteDetailResult.toDto() = run {
    DiaryDetail(
        perfectCount = totalPerfect,
        goodCount = totalGood,
        notGoodCount = totalBad,
        totalScore = totalScore,
        totalKcal = totalKcal,
        workoutInfoList = workoutList.map { info ->
            info.toDto()
        }
    )
}