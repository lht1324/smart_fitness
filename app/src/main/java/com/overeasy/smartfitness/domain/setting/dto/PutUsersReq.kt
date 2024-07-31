package com.overeasy.smartfitness.domain.setting.dto

import kotlinx.serialization.Serializable

@Serializable
data class PutUsersReq(
    val userId: Int? = null,
    val nickname: String? = null,
    val age: Int? = null,
    val height: Float? = null,
    val weight: Float? = null,
    val gender: String? = null,
    val spicyPreference: Int? = null,
    val meatConsumption: Boolean? = null,
    val tastePreference: String? = null,
    val activityLevel: Int? = null,
    val preferenceTypeFood: String? = null,
    val preferenceFoods: String? = null
)
