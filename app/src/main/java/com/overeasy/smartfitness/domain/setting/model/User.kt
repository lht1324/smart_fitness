package com.overeasy.smartfitness.domain.setting.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val token: String? = null
)
