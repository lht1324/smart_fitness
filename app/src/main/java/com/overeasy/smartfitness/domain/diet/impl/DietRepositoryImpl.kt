package com.overeasy.smartfitness.domain.diet.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.diet.entity.GetDietRes
import com.overeasy.smartfitness.simpleGet
import io.ktor.client.*
import javax.inject.Inject

class DietRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : DietRepository {
    private val baseUrl = BuildConfig.BASE_URL

    override suspend fun getDiet(category: String): GetDietRes =
        client.simpleGet("$baseUrl/diet?category=$category") {

        }
}