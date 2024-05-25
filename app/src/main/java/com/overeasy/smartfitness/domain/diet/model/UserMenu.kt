package com.overeasy.smartfitness.domain.diet.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserMenu(
    val userId: Int,
    val dietDate: String,
    val foodName: String
)
