package com.overeasy.smartfitness.domain.workout.dto.res.note

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import kotlinx.serialization.Serializable

@Serializable
data class GetWorkoutNoteListRes(
    override val code: Int = -1,
    override val message: String,
    override val success: Boolean,

    val result: GetWorkoutNoteListResult?,
) : BaseResponseModel