package com.overeasy.smartfitness.domain.ranking

import com.overeasy.smartfitness.domain.ranking.entity.GetRankingListRes

interface RankingRepository {
    suspend fun getRankingList(): GetRankingListRes
}