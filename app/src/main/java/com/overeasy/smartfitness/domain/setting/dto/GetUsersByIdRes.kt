package com.overeasy.smartfitness.domain.setting.dto

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUsersByIdRes(
    override val code: Int = -1,
    @SerialName("message") override val message: String,
    override val success: Boolean,

    @SerialName("result") val result: UserData? = null,
) : BaseResponseModel
