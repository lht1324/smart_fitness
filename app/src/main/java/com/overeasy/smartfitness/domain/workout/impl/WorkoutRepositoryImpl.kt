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
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.simpleGet
import com.overeasy.smartfitness.simplePost
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.onDownload
import io.ktor.client.plugins.onUpload
import io.ktor.client.plugins.timeout
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URLEncoder
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
        client.simplePost("$baseUrl/note/workout") {
            body = Json.encodeToString(req)
        }
    override suspend fun getWorkoutVideoList(noteId: Int): GetWorkoutVideoListRes =
        client.simpleGet("$baseUrl/video/$noteId")
    override suspend fun postWorkoutVideo(
        noteId: Int,
        exerciseName: String,
        videoFileDir: String,
        onProgress: (Long, Long) -> Unit
    ): BaseResponse =
        client.simplePost("$baseUrl/video/$noteId/${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(exerciseName, "UTF-8")
            }
        }") {
            timeout {
                requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
                connectTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
                socketTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            }
            contentType(ContentType.MultiPart.FormData)
            body = MultiPartFormDataContent(
                formData {
                    append(
                        "video",
                        File(videoFileDir).readBytes()
                    )
                }
            )
            onUpload { bytesSentTotal, contentLength ->
                onProgress(bytesSentTotal, contentLength)
                println("jaehoLee", "Ktor Multipart (onUpload): TotalBytes = $bytesSentTotal, ContentLength = $contentLength")
            }
        }

    // API 미완
//    override suspend fun getWorkoutResult(): GetWorkoutResultRes {
//        TODO("Not yet implemented")
//    }
}