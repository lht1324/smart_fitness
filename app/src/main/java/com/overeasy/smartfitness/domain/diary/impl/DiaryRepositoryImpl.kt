package com.overeasy.smartfitness.domain.diary.impl

import com.overeasy.smartfitness.domain.diary.DiaryRepository
import com.overeasy.smartfitness.domain.diary.entity.GetDiaryDetailRes
import com.overeasy.smartfitness.domain.diary.entity.GetDiaryRes
import io.ktor.client.HttpClient
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : DiaryRepository {
    override suspend fun getDiary(): GetDiaryRes {
        TODO("Not yet implemented")
    }

    override suspend fun getDiaryDetail(): GetDiaryDetailRes {
        TODO("Not yet implemented")
    }
}