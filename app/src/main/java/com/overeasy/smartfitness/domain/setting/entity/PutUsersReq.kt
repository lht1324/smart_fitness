package com.overeasy.smartfitness.domain.setting.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.setting.model.User
import com.overeasy.smartfitness.domain.setting.model.UserData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PutUsersReq(
    val id: Int? = null,
    val nickname: String? = null,
    val age: Int? = null,
    val height: Float? = null,
    val weight: Float? = null,
    val spicyPreference: Int? = null,
    val meatConsumption: Boolean? = null,
    val tastePreference: String? = null,
    val activityLevel: Int? = null,
    val preferenceTypeFood: String? = null
)
