package com.overeasy.smartfitness.domain.ai.dto

import com.overeasy.smartfitness.domain.ai.model.LandmarkInfo
import kotlinx.serialization.Serializable

@Serializable
data class PostAiReq(
    val positionList: List<LandmarkInfo>
)