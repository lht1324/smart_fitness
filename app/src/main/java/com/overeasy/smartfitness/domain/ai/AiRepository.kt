package com.overeasy.smartfitness.domain.ai

import com.overeasy.smartfitness.domain.ai.entity.PostAiReq
import com.overeasy.smartfitness.domain.ai.entity.PostAiRes

interface AiRepository {
    suspend fun postAi(req: PostAiReq): PostAiRes
}