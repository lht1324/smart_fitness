package com.overeasy.smartfitness.domain.setting.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.setting.model.UserData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUsersRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean = false,

    val result: String = "",
) : BaseResponseModel
