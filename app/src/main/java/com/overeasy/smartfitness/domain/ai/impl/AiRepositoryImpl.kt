@file:OptIn(InternalAPI::class)

package com.overeasy.smartfitness.domain.ai.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.domain.ai.AiRepository
import com.overeasy.smartfitness.domain.ai.dto.PostAiFeedbackReq
import com.overeasy.smartfitness.domain.ai.dto.PostAiFeedbackRes
import com.overeasy.smartfitness.domain.ai.dto.PostAiReq
import com.overeasy.smartfitness.domain.ai.dto.PostAiRes
import com.overeasy.smartfitness.simplePost
import io.ktor.client.HttpClient
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AiRepositoryImpl(
    private val client: HttpClient
) : AiRepository {
    private val baseUrl = "${BuildConfig.BASE_URL}/ai"

    override suspend fun postAi(req: PostAiReq): PostAiRes =
        client.simplePost(baseUrl) {
            body = Json.encodeToString(req)
        }

    override suspend fun postAiFeedback(req: PostAiFeedbackReq): PostAiFeedbackRes =
        client.simplePost("$baseUrl/feedback") {
            body = Json.encodeToString(req)
        }
}