package com.overeasy.smartfitness.domain.ai.entity

import com.overeasy.smartfitness.domain.ai.model.LandmarkInfo
import com.overeasy.smartfitness.domain.workout.model.workout.LandmarkCoordinate
import kotlinx.serialization.Serializable

@Serializable
data class PostAiReq(
    val positionList: List<LandmarkInfo>
)