package com.overeasy.smartfitness.domain.ai.dto

import kotlinx.serialization.Serializable

@Serializable
data class LandmarkCoordinate(
    val x: Float,
    val y: Float
) {
    operator fun plus(other: LandmarkCoordinate): LandmarkCoordinate {
        return LandmarkCoordinate(
            x = this.x + other.x,
            y = this.y + other.y
        )
    }
    operator fun div(other: Float): LandmarkCoordinate {
        return LandmarkCoordinate(
            x = this.x / other,
            y = this.y / other
        )
    }
}
