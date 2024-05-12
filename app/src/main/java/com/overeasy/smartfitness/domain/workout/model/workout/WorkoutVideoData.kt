package com.overeasy.smartfitness.domain.workout.model.workout

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutVideoData(
    val workoutVideoId: Int,
    val noteId: Int,
    val userId: Int,
    val fileName: String,
    val exerciseName: String,
    val createdAt: String // YYYY-MM-DDT0HH:MM:SS.(locale)
)
