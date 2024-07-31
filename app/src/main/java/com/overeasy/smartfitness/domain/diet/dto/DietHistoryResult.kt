package com.overeasy.smartfitness.domain.diet.dto

import kotlinx.serialization.Serializable

@Serializable
data class DietHistoryResult(
    val dietList: List<DietHistory>
)
