@file:OptIn(InternalAPI::class)

package com.overeasy.smartfitness.domain.workout.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.dto.req.PostWorkoutDataReq
import com.overeasy.smartfitness.domain.workout.dto.res.note.GetWorkoutNoteDetailRes
import com.overeasy.smartfitness.domain.workout.dto.res.note.GetWorkoutNoteListRes
import com.overeasy.smartfitness.domain.workout.dto.res.workout.GetWorkoutVideoListRes
import com.overeasy.smartfitness.domain.workout.dto.res.workout.PostWorkoutNoteRes
import com.overeasy.smartfitness.simpleGet
import com.overeasy.smartfitness.simplePost
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.append
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
    override suspend fun getWorkoutNoteList(date: String): GetWorkoutNoteListRes =
        client.simpleGet("$baseUrl/note/workout/${MainApplication.appPreference.userId}/$date")

    override suspend fun getWorkoutNoteDetail(noteId: Int): GetWorkoutNoteDetailRes =
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
        client.submitFormWithBinaryData(
            formData = formData {
                append(
                    "file",
                    File(videoFileDir).readBytes(),
                    Headers.build {
                        val fileName = videoFileDir.takeLastWhile { it != '/'}

                        append(HttpHeaders.ContentType, ContentType.Video.MP4)
                        append(HttpHeaders.ContentDisposition, "filename=\"${fileName}\"")
                    }
                )
            }
        ) {
            url(
                "$baseUrl/video/$noteId/${
                    withContext(Dispatchers.IO) {
                        URLEncoder.encode(exerciseName, "UTF-8")
                    }
                }"
            )
            contentType(ContentType.MultiPart.FormData)
            onUpload { bytesSentTotal, contentLength ->
                onProgress(bytesSentTotal, contentLength)
            }
        }.run {
            Json.decodeFromString<BaseResponse>(bodyAsText())
        }
}