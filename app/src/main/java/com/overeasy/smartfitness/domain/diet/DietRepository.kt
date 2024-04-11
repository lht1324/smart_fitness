package com.overeasy.smartfitness.domain.diet

import com.overeasy.smartfitness.domain.diet.entity.GetDietCategoryRes

interface DietRepository {
    suspend fun getDietCategory(): GetDietCategoryRes
}