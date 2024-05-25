package com.overeasy.smartfitness.domain.foods.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.domain.foods.FoodsRepository
import com.overeasy.smartfitness.domain.foods.entity.GetFoodsInitRes
import com.overeasy.smartfitness.simpleGet
import io.ktor.client.HttpClient
import javax.inject.Inject

class FoodsRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : FoodsRepository {
    private val baseUrl = "${BuildConfig.BASE_URL}/foods"
    override suspend fun getFoodsInit(): GetFoodsInitRes =
        client.simpleGet("$baseUrl/init")
}