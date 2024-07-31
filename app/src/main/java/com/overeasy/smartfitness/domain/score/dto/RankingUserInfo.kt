package com.overeasy.smartfitness.domain.score.dto

import kotlinx.serialization.Serializable

@Serializable
data class RankingUserInfo(
    val nickname: String,
    val score: Int,
    val exerciseName: String,
    val ranking: Int
)
