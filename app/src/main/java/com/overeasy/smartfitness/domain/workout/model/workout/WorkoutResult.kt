package com.overeasy.smartfitness.domain.workout.model.workout

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutResult(
    val workoutList: List<Workout>,
    val workoutScoreList: List<Score>,
    val workoutTotalScore: Int,
    val menuList: List<Menu>? = null
)
