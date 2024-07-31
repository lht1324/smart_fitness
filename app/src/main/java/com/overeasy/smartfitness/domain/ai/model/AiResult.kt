package com.overeasy.smartfitness.domain.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class AiResult(
    val perfect: Int,
    val good: Int,
    val bad: Int
)
