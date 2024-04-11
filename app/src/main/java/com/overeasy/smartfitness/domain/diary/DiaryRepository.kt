package com.overeasy.smartfitness.domain.diary

import com.overeasy.smartfitness.domain.diary.entity.GetDiaryDetailRes
import com.overeasy.smartfitness.domain.diary.entity.GetDiaryRes

interface DiaryRepository {
    suspend fun getDiary(): GetDiaryRes
    suspend fun getDiaryDetail(): GetDiaryDetailRes
}