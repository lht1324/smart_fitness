@file:OptIn(InternalAPI::class)

package com.overeasy.smartfitness.domain.exercises.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.domain.exercises.ExercisesRepository
import com.overeasy.smartfitness.domain.exercises.dto.GetExercisesRes
import com.overeasy.smartfitness.simpleGet
import io.ktor.client.HttpClient
import io.ktor.util.InternalAPI
import javax.inject.Inject

class ExercisesRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : ExercisesRepository {
    private val baseUrl = BuildConfig.BASE_URL
    override suspend fun getExercises(): GetExercisesRes =
        client.simpleGet("$baseUrl/exercises")

}