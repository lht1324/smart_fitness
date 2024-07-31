package com.overeasy.smartfitness.domain.diet.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostDietsReq(
    val dietList: List<UserMenu>
)
