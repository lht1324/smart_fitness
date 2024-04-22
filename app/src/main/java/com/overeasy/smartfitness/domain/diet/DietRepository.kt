package com.overeasy.smartfitness.domain.diet

import com.overeasy.smartfitness.domain.diet.entity.GetDietRes

interface DietRepository {
    suspend fun getDiet(category: String): GetDietRes
}