package com.overeasy.smartfitness.domain.score.dto

import kotlinx.serialization.Serializable

@Serializable
data class RankingInfo(
    val nickname: String,
    val score: Int
)
