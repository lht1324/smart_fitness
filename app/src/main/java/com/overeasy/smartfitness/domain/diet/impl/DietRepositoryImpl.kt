@file:OptIn(InternalAPI::class)

package com.overeasy.smartfitness.domain.diet.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.diet.entity.GetDietRecommendRes
import com.overeasy.smartfitness.domain.diet.entity.GetDietsHistoryRes
import com.overeasy.smartfitness.domain.diet.entity.PostDietsReq
import com.overeasy.smartfitness.simpleGet
import com.overeasy.smartfitness.simplePost
import io.ktor.client.*
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DietRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : DietRepository {
    private val baseUrl = "${BuildConfig.BASE_URL}/diets"

    override suspend fun getDietsRecommend(userId: Int): GetDietRecommendRes =
        client.simpleGet("$baseUrl/$userId") {

        }

    override suspend fun postDiets(userId: Int, req: PostDietsReq): BaseResponse =
        client.simplePost("$baseUrl/$userId") {
            body = Json.encodeToString(req)
        }

    /**
     * dietDate: yyyy-mm-dd
     */
    override suspend fun getDietsHistory(userId: Int, dietDate: String): GetDietsHistoryRes =
        client.simpleGet("$baseUrl/$userId/$dietDate") {

        }
}