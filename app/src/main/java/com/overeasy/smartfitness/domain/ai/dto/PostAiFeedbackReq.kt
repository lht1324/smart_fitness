package com.overeasy.smartfitness.domain.ai.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostAiFeedbackReq(
    val workoutName: String,
    val workoutResultIndexList: List<List<Int>>
)
