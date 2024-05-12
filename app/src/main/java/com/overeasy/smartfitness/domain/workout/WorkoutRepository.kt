package com.overeasy.smartfitness.domain.workout

import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.workout.entity.GetDiaryDetailRes
import com.overeasy.smartfitness.domain.workout.entity.GetDiaryRes
import com.overeasy.smartfitness.domain.workout.entity.GetWorkoutResultRes
import com.overeasy.smartfitness.domain.workout.entity.GetWorkoutVideoListRes
import com.overeasy.smartfitness.domain.workout.entity.PostWorkoutDataReq
import com.overeasy.smartfitness.domain.workout.entity.PostWorkoutNoteRes

interface WorkoutRepository {
    // 일지
    suspend fun getWorkoutNoteList(date: String): GetDiaryRes
    suspend fun getWorkoutNoteDetail(noteId: Int): GetDiaryDetailRes

    // 운동
    suspend fun postWorkoutNote(userId: Int): PostWorkoutNoteRes
    suspend fun postWorkoutData(req: PostWorkoutDataReq): BaseResponse
    suspend fun getWorkoutVideoList(noteId: Int): GetWorkoutVideoListRes
    suspend fun postWorkoutVideo(noteId: Int, exerciseName: String): BaseResponse

    // API 미완
//    suspend fun getWorkoutResult(): GetWorkoutResultRes
}