package com.overeasy.smartfitness.domain.ranking.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.domain.ranking.ScoreRepository
import com.overeasy.smartfitness.domain.ranking.entity.GetScoresRes
import com.overeasy.smartfitness.domain.ranking.entity.GetScoresByExerciseNameRes
import com.overeasy.smartfitness.domain.ranking.entity.GetScoresUserByExerciseNameRes
import com.overeasy.smartfitness.simpleGet
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import javax.inject.Inject

class ScoreRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : ScoreRepository {
    private val baseUrl = "${BuildConfig.BASE_URL}/scores"

    override suspend fun getScores(): GetScoresRes =
        client.simpleGet(baseUrl)

    override suspend fun getScoresByExerciseName(exerciseName: String): GetScoresByExerciseNameRes =
        client.simpleGet("$baseUrl/${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(exerciseName, "UTF-8")
            }
        }")

    override suspend fun getScoresUserByExerciseName(
        userId: Int,
        exerciseName: String
    ): GetScoresUserByExerciseNameRes =
        client.simpleGet("$baseUrl/user/$userId/${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(exerciseName, "UTF-8")
            }
        }")
}