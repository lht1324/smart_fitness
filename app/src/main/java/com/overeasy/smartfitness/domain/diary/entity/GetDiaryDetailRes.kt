package com.overeasy.smartfitness.domain.diary.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.diary.model.DietMenu
import com.overeasy.smartfitness.domain.diary.model.Workout
import kotlinx.serialization.Serializable

@Serializable
data class GetDiaryDetailRes(
    override val code: Int,
    override val message: String,
    val workoutScore: Int,
    val dietMenuList: List<DietMenu>,
    val workoutList: List<Workout>,

    override val success: Boolean = false,
    override val error: String? = null,
    override val timestamp: String? = null,
    override val path: String? = null
) : BaseResponseModel
