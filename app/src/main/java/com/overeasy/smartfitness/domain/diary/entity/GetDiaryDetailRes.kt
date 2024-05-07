package com.overeasy.smartfitness.domain.diary.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.diary.model.DiaryDetailWorkoutInfo
import com.overeasy.smartfitness.domain.diary.model.DietMenu
import com.overeasy.smartfitness.domain.diary.model.Workout
import kotlinx.serialization.Serializable

@Serializable
data class GetDiaryDetailRes(
    override val code: Int,
    override val message: String,

    val totalKcal: Int = 0,
    val totalPerfect: Int = 0,
    val totalGood: Int = 0,
    val totalBad: Int = 0,
    val totalScore: Int = 0,
    val workoutList: List<DiaryDetailWorkoutInfo> = listOf(),

    override val success: Boolean = false,
    override val error: String? = null,
    override val timestamp: String? = null,
    override val path: String? = null
) : BaseResponseModel
