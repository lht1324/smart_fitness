package com.overeasy.smartfitness.domain.diet.dto

import kotlinx.serialization.Serializable

@Serializable
data class DietRecommendResult(
    val foodRecommend: List<FoodRecommend>
)
