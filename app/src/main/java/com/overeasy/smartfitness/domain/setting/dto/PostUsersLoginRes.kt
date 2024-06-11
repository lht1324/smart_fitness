package com.overeasy.smartfitness.domain.setting.dto

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostUsersLoginRes(
    override val code: Int = -1,
    @SerialName("message") override val message: String,
    override val success: Boolean = false,

    @SerialName("result") val result: User? = null,
) : BaseResponseModel
