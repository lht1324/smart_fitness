package com.overeasy.smartfitness.domain.score

import com.overeasy.smartfitness.domain.score.dto.GetScoresByExerciseNameRes
import com.overeasy.smartfitness.domain.score.dto.GetScoresUserByExerciseNameRes

interface ScoreRepository {
    suspend fun getScoresByExerciseName(exerciseName: String): GetScoresByExerciseNameRes
    suspend fun getScoresUserByExerciseName(userId: Int, exerciseName: String): GetScoresUserByExerciseNameRes
}