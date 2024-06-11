package com.overeasy.smartfitness.domain.setting.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetUsersReq(
    val nickname: String,
    val age: Int
)
