package com.overeasy.smartfitness.domain.ai.dto

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.Serializable

@Serializable
data class PostAiFeedbackRes(
    override val code: Int = -1,
    override val message: String = "",
    override val success: Boolean = false,

    val result: PostAiFeedbackResult
) : BaseResponseModel
