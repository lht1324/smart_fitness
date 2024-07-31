package com.overeasy.smartfitness.domain.workout.dto.res.note

import kotlinx.serialization.Serializable

@Serializable
data class GetWorkoutNoteListResult(
    val noteList: List<Note>
)