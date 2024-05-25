package com.overeasy.smartfitness.domain.diet.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class DietRecommendResult(
    val foodRecommend: List<RecommendedFood>
)
