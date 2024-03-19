package com.overeasy.smartfitness.domain.ranking.model

import kotlinx.serialization.Serializable

@Serializable
data class RankingItem(
    val nickname: String,
    val score: Int,
    val tier: String
)
