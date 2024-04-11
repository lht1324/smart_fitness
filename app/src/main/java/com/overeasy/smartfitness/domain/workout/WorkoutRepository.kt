package com.overeasy.smartfitness.domain.workout

import com.overeasy.smartfitness.domain.workout.entity.GetWorkoutResultRes

interface WorkoutRepository {
    suspend fun getWorkoutResult(): GetWorkoutResultRes
}