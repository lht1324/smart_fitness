package com.overeasy.smartfitness.domain.diary.entity

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.diary.model.DiaryResult
import kotlinx.serialization.Serializable

@Serializable
data class GetDiaryRes(
    override val code: Int = -1,
    override val message: String,
    val result: DiaryResult?,

    override val success: Boolean = false,
    override val error: String? = null,
    override val timestamp: String? = null,
    override val path: String? = null
) : BaseResponseModel