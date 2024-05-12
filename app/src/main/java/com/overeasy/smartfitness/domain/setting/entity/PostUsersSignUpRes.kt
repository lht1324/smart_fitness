package com.overeasy.smartfitness.domain.setting.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.setting.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostUsersSignUpRes(
    override val code: Int = -1,
    @SerialName("message") override val message: String,
    override val success: Boolean,

    @SerialName("result") val result: User? = null,
) : BaseResponseModel
