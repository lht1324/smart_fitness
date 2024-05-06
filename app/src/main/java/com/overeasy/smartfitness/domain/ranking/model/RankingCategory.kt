package com.overeasy.smartfitness.domain.ranking.model

import kotlinx.serialization.Serializable

@Serializable
data class RankingCategory(
    val scoreCategory: List<String>
)
