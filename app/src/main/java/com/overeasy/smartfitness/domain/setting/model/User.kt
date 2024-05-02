package com.overeasy.smartfitness.domain.setting.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null, // 문의 ㄱㄱ
    val name: String? = null,
    val password: String? = null,
    val token: String? = null
)
