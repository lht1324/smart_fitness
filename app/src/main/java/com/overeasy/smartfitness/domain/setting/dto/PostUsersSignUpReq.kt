package com.overeasy.smartfitness.domain.setting.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostUsersSignUpReq(
    val username: String,
    val password: String,
    val nickname: String,
    val age: Int?,
    val weight: Float?,
    val height: Float?,
    val gender: String,
    val spicyPreference: Int?,
    val meatConsumption: Boolean?,
    val tastePreference: String?,
    val activityLevel: Int?,
    val preferenceTypeFood: String?,
    val preferenceFoods: String?
)
