package com.overeasy.smartfitness.domain.diet.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostDietsRecommendSelectReq(
    val userId: Int,
    val consumedFoodNames: List<String>
)
