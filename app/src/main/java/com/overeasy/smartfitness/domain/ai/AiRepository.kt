package com.overeasy.smartfitness.domain.ai

import com.overeasy.smartfitness.domain.ai.dto.PostAiFeedbackReq
import com.overeasy.smartfitness.domain.ai.dto.PostAiFeedbackRes
import com.overeasy.smartfitness.domain.ai.dto.PostAiReq
import com.overeasy.smartfitness.domain.ai.dto.PostAiRes

interface AiRepository {
    suspend fun postAi(req: PostAiReq): PostAiRes

    suspend fun postAiFeedback(req: PostAiFeedbackReq): PostAiFeedbackRes
}