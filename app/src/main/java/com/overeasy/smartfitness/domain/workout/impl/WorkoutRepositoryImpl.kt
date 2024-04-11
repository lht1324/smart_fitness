package com.overeasy.smartfitness.domain.workout.impl

import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.entity.GetWorkoutResultRes
import io.ktor.client.HttpClient
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : WorkoutRepository {
    override suspend fun getWorkoutResult(): GetWorkoutResultRes {
        TODO("Not yet implemented")
    }
}