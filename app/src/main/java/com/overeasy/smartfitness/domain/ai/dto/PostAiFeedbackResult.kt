package com.overeasy.smartfitness.domain.ai.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostAiFeedbackResult(
    val feedback: List<String>
)
