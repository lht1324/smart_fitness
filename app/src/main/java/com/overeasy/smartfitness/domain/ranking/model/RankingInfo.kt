package com.overeasy.smartfitness.domain.ranking.model

import kotlinx.serialization.Serializable

@Serializable
data class RankingInfo(
    val nickname: String,
    val score: Int
)
