package com.overeasy.smartfitness.domain.workout.model.diary

import kotlinx.serialization.Serializable

@Serializable
data class DiaryResult(
    val noteList: List<Note>
)