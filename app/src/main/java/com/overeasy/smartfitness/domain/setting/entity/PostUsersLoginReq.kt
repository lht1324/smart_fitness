package com.overeasy.smartfitness.domain.setting.entity

import kotlinx.serialization.Serializable

@Serializable
data class PostUsersLoginReq(
    val username: String,
    val password: String
)
