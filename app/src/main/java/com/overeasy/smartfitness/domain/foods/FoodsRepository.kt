package com.overeasy.smartfitness.domain.foods

import com.overeasy.smartfitness.domain.foods.dto.GetFoodsInitRes

interface FoodsRepository {
    suspend fun getFoodsInit(): GetFoodsInitRes
}