package com.overeasy.smartfitness.domain.workout.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.workout.model.diary.DiaryDetailWorkoutInfo
import kotlinx.serialization.Serializable

@Serializable
data class GetDiaryDetailRes(
    override val code: Int,
    override val message: String,
    override val success: Boolean,

    val totalKcal: Int = 0,
    val totalPerfect: Int = 0,
    val totalGood: Int = 0,
    val totalBad: Int = 0,
    val totalScore: Int = 0,
    val workoutList: List<DiaryDetailWorkoutInfo> = listOf()
) : BaseResponseModel
