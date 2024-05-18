package com.overeasy.smartfitness.api

import com.overeasy.smartfitness.domain.base.BaseResponseModel
import com.overeasy.smartfitness.domain.base.ResponseResult
import com.overeasy.smartfitness.println
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

object ApiRequestHelper {
    suspend fun <R : BaseResponseModel> makeRequest(onResponse: suspend () -> R) : ResponseResult<R> {
        return try {
            withContext(Dispatchers.IO) {
                ResponseResult.success(onResponse())
            }
        } catch (exception: CancellationException) {
            println("jaehoLee", "CancellationException")
            ResponseResult.error(exception.fillInStackTrace())
        } catch (exception: Exception) {
            ResponseResult.error(exception.fillInStackTrace())
        }
    }
}