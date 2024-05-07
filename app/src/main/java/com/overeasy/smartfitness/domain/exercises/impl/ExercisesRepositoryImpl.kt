@file:OptIn(InternalAPI::class)

package com.overeasy.smartfitness.domain.exercises.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.diary.DiaryRepository
import com.overeasy.smartfitness.domain.diary.entity.GetDiaryDetailRes
import com.overeasy.smartfitness.domain.diary.entity.GetDiaryRes
import com.overeasy.smartfitness.domain.exercises.ExercisesRepository
import com.overeasy.smartfitness.domain.exercises.entity.GetExercisesRes
import com.overeasy.smartfitness.domain.exercises.entity.PostExercisesReq
import com.overeasy.smartfitness.simpleGet
import com.overeasy.smartfitness.simplePost
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ExercisesRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : ExercisesRepository {
    private val baseUrl = BuildConfig.BASE_URL
    override suspend fun getExercises(): GetExercisesRes =
        client.simpleGet("$baseUrl/exercises")

    override suspend fun postExercises(req: PostExercisesReq): BaseResponse =
        client.simplePost("$baseUrl/exercises") {
            body = Json.encodeToString(req)
        }

    override suspend fun deleteExercises(exerciseName: String): BaseResponse {
        TODO("Not yet implemented")
    }

    override suspend fun postExercisesVideo(exerciseName: String): BaseResponse {
        TODO("Not yet implemented")
    }

    override suspend fun deleteExercisesVideo(exerciseName: String): BaseResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getExercisesVideoStream(exerciseName: String): BaseResponse {
        TODO("Not yet implemented")
    }

}