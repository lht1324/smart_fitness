package com.overeasy.smartfitness.domain.diet

import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.diet.entity.GetDietRecommendRes
import com.overeasy.smartfitness.domain.diet.entity.GetDietsHistoryRes
import com.overeasy.smartfitness.domain.diet.entity.PostDietsReq

interface DietRepository {
    suspend fun getDietsRecommend(userId: Int): GetDietRecommendRes
    suspend fun postDiets(userId: Int, req: PostDietsReq): BaseResponse
    suspend fun getDietsHistory(userId: Int, dietDate: String): GetDietsHistoryRes
}