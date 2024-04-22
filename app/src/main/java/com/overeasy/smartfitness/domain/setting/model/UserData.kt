package com.overeasy.smartfitness.domain.setting.model

import android.provider.ContactsContract.CommonDataKinds.Nickname
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val username: String,
    val nickname: String,
    val age: Int? = null,
    val height: Float? = null,
    val weight: Float? = null,
    val spicyPreference: Int? = null,
    val meatConsumption: Boolean? = null,
    val tastePreference: String? = null,
    val activityLevel: Int? = null,
    val preferenceTypeFood: String? = null
)