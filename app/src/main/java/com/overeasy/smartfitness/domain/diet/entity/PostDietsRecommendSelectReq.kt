package com.overeasy.smartfitness.domain.diet.entity

import kotlinx.serialization.Serializable

@Serializable
data class PostDietsRecommendSelectReq(
    val userId: Int,
    val consumedFoodNames: List<String>
)
