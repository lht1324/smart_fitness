package com.overeasy.smartfitness.domain.ranking.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.domain.ranking.RankingRepository
import com.overeasy.smartfitness.domain.ranking.entity.GetRankingCategoryRes
import com.overeasy.smartfitness.domain.ranking.entity.GetRankingRes
import com.overeasy.smartfitness.domain.ranking.entity.GetRankingUserRes
import com.overeasy.smartfitness.simpleGet
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import javax.inject.Inject

class RankingRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : RankingRepository {
    private val baseUrl = BuildConfig.BASE_URL

    override suspend fun getRankingCategory(): GetRankingCategoryRes =
        client.simpleGet("$baseUrl/scores")

    override suspend fun getRanking(exerciseName: String): GetRankingRes =
        client.simpleGet("$baseUrl/scores/${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(exerciseName, "UTF-8")
            }
        }")

    override suspend fun getRankingUser(
        userId: Int,
        exerciseName: String
    ): GetRankingUserRes =
        client.simpleGet("$baseUrl/scores/$userId/${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(exerciseName, "UTF-8")
            }
        }")
}