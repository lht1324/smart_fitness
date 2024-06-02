package com.overeasy.smartfitness.domain.workout.dto.res.workout

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutVideoData(
    val workoutVideoId: Int,
    val noteId: Int,
    val userId: Int,
    val fileName: String,
    val exerciseName: String,
    val createdAt: List<Int>
)
