package com.overeasy.smartfitness.domain.diary.model

import kotlinx.serialization.Serializable

@Serializable
data class DiaryResult(
    val noteList: List<Note>
)