package com.overeasy.smartfitness.domain.exercises.dto

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.Serializable

@Serializable
data class GetExercisesRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean = false,

    val result: GetExerciseResult? = null,
) : BaseResponseModel
