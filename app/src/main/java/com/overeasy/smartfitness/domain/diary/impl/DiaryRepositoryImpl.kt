package com.overeasy.smartfitness.domain.diary.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.diary.DiaryRepository
import com.overeasy.smartfitness.domain.diary.entity.GetDiaryDetailRes
import com.overeasy.smartfitness.domain.diary.entity.GetDiaryRes
import com.overeasy.smartfitness.simpleGet
import io.ktor.client.HttpClient
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : DiaryRepository {
    private val baseUrl = BuildConfig.BASE_URL

    override suspend fun getDiary(date: String): GetDiaryRes =
        client.simpleGet("$baseUrl/workouts/note/workout/${MainApplication.appPreference.userId}/$date")

    override suspend fun getDiaryDetail(noteId: String): GetDiaryDetailRes =
        client.simpleGet("$baseUrl/workouts/note/$noteId")
}