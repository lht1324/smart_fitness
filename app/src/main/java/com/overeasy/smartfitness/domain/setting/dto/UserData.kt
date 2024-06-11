package com.overeasy.smartfitness.domain.setting.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val username: String,
    val nickname: String,
    val age: Int,
    val height: Float,
    val weight: Float,
    val gender: String,
    val spicyPreference: Int? = null,
    val meatConsumption: Boolean? = null,
    val tastePreference: String? = null,
    val activityLevel: Int,
    val preferenceTypeFood: String? = null,
    val preferenceFoods: String?
)