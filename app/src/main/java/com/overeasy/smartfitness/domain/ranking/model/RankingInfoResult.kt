package com.overeasy.smartfitness.domain.ranking.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankingInfoResult(
    val exerciseName: String,
    @SerialName("userScoreVOList") val rankingInfoList: List<RankingInfo>
)
