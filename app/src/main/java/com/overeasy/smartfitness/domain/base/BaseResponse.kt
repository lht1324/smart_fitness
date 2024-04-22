package com.overeasy.smartfitness.domain.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse(
    override val code: Int = -1,
    override val message: String = "",

    override val success: Boolean = false,
    val result: String = "",
    override val error: String? = null,
    override val timestamp: String? = null,
    override val path: String? = null,
    val status: Int? = null
) : BaseResponseModel