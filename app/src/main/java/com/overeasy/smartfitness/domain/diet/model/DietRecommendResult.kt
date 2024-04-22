package com.overeasy.smartfitness.domain.diet.model

import kotlinx.serialization.Serializable

@Serializable
data class DietRecommendResult(
    val foodRecommend: List<String>,
    val totalCalorie: Int
)
