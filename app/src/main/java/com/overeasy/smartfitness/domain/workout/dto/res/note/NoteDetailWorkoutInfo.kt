package com.overeasy.smartfitness.domain.workout.dto.res.note

import com.overeasy.smartfitness.domain.workout.entity.DiaryDetailWorkoutInfo
import kotlinx.serialization.Serializable

@Serializable
data class NoteDetailWorkoutInfo(
    val workoutId: Int,
    val noteId: Int,
    val exerciseName: String,
    val setNum: Int,
    val repeats: Int,
    val weight: Int
)

fun NoteDetailWorkoutInfo.toEntity() = run {
    DiaryDetailWorkoutInfo(
        noteId = noteId,
        workoutName = exerciseName,
        setCount = setNum,
        repeatCount = repeats,
        weight = weight
    )
}
