package com.overeasy.smartfitness.domain.diet.impl

import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.diet.entity.GetDietCategoryRes
import io.ktor.client.*
import javax.inject.Inject

class DietRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : DietRepository {
    //    override suspend fun getDietCategory(): GetDietCategory =
//        httpClient.get("") {
//            parameter("", "")
//        }.body()
    override suspend fun getDietCategory(): GetDietCategoryRes {
        TODO("Not yet implemented")
    }
}