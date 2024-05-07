package com.overeasy.smartfitness.domain.exercises

import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.exercises.entity.GetExercisesRes
import com.overeasy.smartfitness.domain.exercises.entity.PostExercisesReq

interface ExercisesRepository {
    suspend fun getExercises(): GetExercisesRes
    suspend fun postExercises(req: PostExercisesReq): BaseResponse
    suspend fun deleteExercises(exerciseName: String): BaseResponse
    suspend fun postExercisesVideo(exerciseName: String): BaseResponse
    suspend fun deleteExercisesVideo(exerciseName: String): BaseResponse
    suspend fun getExercisesVideoStream(exerciseName: String): BaseResponse
}