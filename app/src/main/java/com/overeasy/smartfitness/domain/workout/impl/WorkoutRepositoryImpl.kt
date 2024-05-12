@file:OptIn(InternalAPI::class)

package com.overeasy.smartfitness.domain.workout.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.workout.entity.GetDiaryDetailRes
import com.overeasy.smartfitness.domain.workout.entity.GetDiaryRes
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.entity.GetWorkoutResultRes
import com.overeasy.smartfitness.domain.workout.entity.GetWorkoutVideoListRes
import com.overeasy.smartfitness.domain.workout.entity.PostWorkoutDataReq
import com.overeasy.smartfitness.domain.workout.entity.PostWorkoutNoteRes
import com.overeasy.smartfitness.simpleGet
import com.overeasy.smartfitness.simplePost
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.util.InternalAPI
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val client: HttpClient,
) : WorkoutRepository {
    private val baseUrl = "${BuildConfig.BASE_URL}/workouts"

    // 일지
    override suspend fun getWorkoutNoteList(date: String): GetDiaryRes =
        client.simpleGet("$baseUrl/note/workout/${MainApplication.appPreference.userId}/$date")

    override suspend fun getWorkoutNoteDetail(noteId: Int): GetDiaryDetailRes =
        client.simpleGet("$baseUrl/note/$noteId")

    // 운동
    override suspend fun postWorkoutNote(userId: Int): PostWorkoutNoteRes =
        client.simplePost("$baseUrl/note/$userId")
    override suspend fun postWorkoutData(req: PostWorkoutDataReq): BaseResponse =
        client.simplePost("$baseUrl/note/workout")
    override suspend fun getWorkoutVideoList(noteId: Int): GetWorkoutVideoListRes =
        client.simpleGet("$baseUrl/video/$noteId")
    override suspend fun postWorkoutVideo(noteId: Int, exerciseName: String): BaseResponse =
        client.submitFormWithBinaryData(
            url = "$baseUrl/video/$noteId/$exerciseName",
            formData = formData {
                Headers.build {
                    append(HttpHeaders.ContentType, ContentType.Video.MP4)
                }
            }
        ).run {
            Json.decodeFromString<BaseResponse>(bodyAsText())
        }

    // API 미완
//    override suspend fun getWorkoutResult(): GetWorkoutResultRes {
//        TODO("Not yet implemented")
//    }
}