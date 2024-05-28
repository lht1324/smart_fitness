package com.overeasy.smartfitness.domain.ranking

import com.overeasy.smartfitness.domain.ranking.entity.GetScoresRes
import com.overeasy.smartfitness.domain.ranking.entity.GetScoresByExerciseNameRes
import com.overeasy.smartfitness.domain.ranking.entity.GetScoresUserByExerciseNameRes

interface ScoreRepository {
    suspend fun getScores(): GetScoresRes
    suspend fun getScoresByExerciseName(exerciseName: String): GetScoresByExerciseNameRes
    suspend fun getScoresUserByExerciseName(userId: Int, exerciseName: String): GetScoresUserByExerciseNameRes
}