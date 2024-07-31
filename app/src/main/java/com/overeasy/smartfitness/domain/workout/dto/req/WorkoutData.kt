package com.overeasy.smartfitness.domain.workout.dto.req

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutData(
    val setNum: Int,
    val repeats: Int?,
    val weight: Int?,
    val scorePerfect: Int = 0,
    val scoreGood: Int = 0,
    val scoreBad: Int = 0
)
