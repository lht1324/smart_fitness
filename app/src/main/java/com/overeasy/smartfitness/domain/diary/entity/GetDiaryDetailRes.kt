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

    val totalKcal: Int,
    val totalPerfect: Int,
    val totalGood: Int,
    val totalBad: Int,
    val totalScore: Int,
    val workoutList: List<DiaryDetailWorkoutInfo>,

    override val success: Boolean = false,
    override val error: String? = null,
    override val timestamp: String? = null,
    override val path: String? = null
) : BaseResponseModel
