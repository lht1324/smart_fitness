package com.overeasy.smartfitness.domain.base

interface BaseResponseModel {
    val status: Int
    val message: String

    val success: Boolean
    val error: String?
    val timestamp: String?
    val path: String?
}