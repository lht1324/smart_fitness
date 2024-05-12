package com.overeasy.smartfitness.domain.ai.entity

import com.overeasy.smartfitness.domain.ai.model.AiResult
import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostAiRes(
    override val code: Int = -1,
    override val message: String = "",
    override val success: Boolean = false,

    val result: AiResult
) : BaseResponseModel