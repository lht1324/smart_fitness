package com.overeasy.smartfitness.domain.diet.entity

import com.google.gson.annotations.SerializedName
import com.overeasy.smartfitness.domain.diet.model.UserMenu
import kotlinx.serialization.Serializable

@Serializable
data class PostDietsReq(
    val dietList: List<UserMenu>
)
