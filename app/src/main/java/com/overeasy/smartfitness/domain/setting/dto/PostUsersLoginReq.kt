package com.overeasy.smartfitness.domain.setting.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostUsersLoginReq(
    val username: String,
    val password: String
)
