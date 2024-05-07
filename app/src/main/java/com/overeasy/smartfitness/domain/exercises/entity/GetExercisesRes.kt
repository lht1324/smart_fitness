package com.overeasy.smartfitness.domain.exercises.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.exercises.model.GetExerciseResult
import kotlinx.serialization.Serializable

@Serializable
data class GetExercisesRes(
    override val code: Int = -1,
    override val message: String,
    val result: GetExerciseResult? = null,

    override val success: Boolean = false,
    override val error: String? = null,
    override val timestamp: String? = null,
    override val path: String? = null
) : BaseResponseModel
