package com.overeasy.smartfitness.domain.exercises

import com.overeasy.smartfitness.domain.exercises.dto.GetExercisesRes

interface ExercisesRepository {
    suspend fun getExercises(): GetExercisesRes
}