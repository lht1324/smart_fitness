package com.overeasy.smartfitness.domain.foods

import com.overeasy.smartfitness.domain.foods.entity.GetFoodsInitRes

interface FoodsRepository {
    suspend fun getFoodsInit(): GetFoodsInitRes
}