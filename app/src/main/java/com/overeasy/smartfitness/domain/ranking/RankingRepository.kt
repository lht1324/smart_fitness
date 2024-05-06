package com.overeasy.smartfitness.domain.ranking

import com.overeasy.smartfitness.domain.ranking.entity.GetRankingCategoryRes
import com.overeasy.smartfitness.domain.ranking.entity.GetRankingRes
import com.overeasy.smartfitness.domain.ranking.entity.GetRankingUserRes

interface RankingRepository {
    suspend fun getRankingCategory(): GetRankingCategoryRes
    suspend fun getRanking(exerciseName: String): GetRankingRes
    suspend fun getRankingUser(userId: Int, exerciseName: String): GetRankingUserRes
}