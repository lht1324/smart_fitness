package com.overeasy.smartfitness.domain.setting.entity

import kotlinx.serialization.Serializable

@Serializable
data class GetUsersReq(
    val nickname: String,
    val age: Int
)
