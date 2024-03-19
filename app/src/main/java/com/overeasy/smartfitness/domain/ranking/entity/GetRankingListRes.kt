package com.overeasy.smartfitness.domain.ranking.entity

import com.overeasy.smartfitness.domain.ranking.model.RankingItem
import kotlinx.serialization.Serializable

@Serializable
data class GetRankingListRes(
    val rankingList: List<RankingItem>
)
