package com.overeasy.smartfitness.domain.workout

import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.workout.dto.req.PostWorkoutDataReq
import com.overeasy.smartfitness.domain.workout.dto.res.note.GetWorkoutNoteDetailRes
import com.overeasy.smartfitness.domain.workout.dto.res.note.GetWorkoutNoteListRes
import com.overeasy.smartfitness.domain.workout.dto.res.workout.GetWorkoutVideoListRes
import com.overeasy.smartfitness.domain.workout.dto.res.workout.PostWorkoutNoteRes

interface WorkoutRepository {
    // 일지
    suspend fun getWorkoutNoteList(date: String): GetWorkoutNoteListRes
    suspend fun getWorkoutNoteDetail(noteId: Int): GetWorkoutNoteDetailRes

    // 운동
    suspend fun postWorkoutNote(userId: Int): PostWorkoutNoteRes
    suspend fun postWorkoutData(req: PostWorkoutDataReq): BaseResponse
    suspend fun getWorkoutVideoList(noteId: Int): GetWorkoutVideoListRes
    suspend fun postWorkoutVideo(
        noteId: Int,
        exerciseName: String,
        videoFileDir: String,
        onProgress: (Long, Long) -> Unit
    ): BaseResponse

    // API 미완
//    suspend fun getWorkoutResult(): GetWorkoutResultRes
}