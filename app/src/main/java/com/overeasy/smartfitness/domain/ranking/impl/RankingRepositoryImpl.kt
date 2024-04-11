package com.overeasy.smartfitness.domain.ranking.impl

import com.overeasy.smartfitness.domain.ranking.RankingRepository
import com.overeasy.smartfitness.domain.ranking.entity.GetRankingListRes
import io.ktor.client.HttpClient
import javax.inject.Inject

class RankingRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : RankingRepository {
    override suspend fun getRankingList(): GetRankingListRes {
        TODO("Not yet implemented")
    }
}