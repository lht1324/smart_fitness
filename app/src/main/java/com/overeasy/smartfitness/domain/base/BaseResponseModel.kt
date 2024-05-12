package com.overeasy.smartfitness.domain.base

interface BaseResponseModel {
    val code: Int
    val message: String

    val success: Boolean
}