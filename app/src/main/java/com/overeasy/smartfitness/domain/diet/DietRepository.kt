package com.overeasy.smartfitness.domain.diet

import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.diet.dto.GetDietRecommendRes
import com.overeasy.smartfitness.domain.diet.entity.GetDietsHistoryRes
import com.overeasy.smartfitness.domain.diet.entity.PostDietsRecommendSelectReq
import com.overeasy.smartfitness.domain.diet.entity.PostDietsReq

interface DietRepository {
    suspend fun postDietsRecommend(req: PostDietsRecommendSelectReq): GetDietRecommendRes
    suspend fun postDiets(userId: Int, req: PostDietsReq): BaseResponse
    suspend fun getDietsHistory(userId: Int, dietDate: String): GetDietsHistoryRes
    suspend fun postDietsRecommendSelect(req: PostDietsRecommendSelectReq): BaseResponse
}