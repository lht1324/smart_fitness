package com.overeasy.smartfitness.domain.diet.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class DietHistoryResult(
    @SerializedName("dietList") val dietHistoryList: List<DietHistory>
)
