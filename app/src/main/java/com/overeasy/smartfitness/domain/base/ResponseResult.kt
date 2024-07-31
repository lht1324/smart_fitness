package com.overeasy.smartfitness.domain.base

import com.overeasy.smartfitness.println
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class ResponseResult<out T> internal constructor(val response: Any?) {
    val responseResultType get() = when {
        isSuccess -> ResponseResultType.Success
        isFailure -> ResponseResultType.Failure
        isError -> ResponseResultType.Error
        else -> ResponseResultType.Error
    }

    val isSuccess get(): Boolean = response.run { this is BaseResponseModel && success } // code == 200 }
    val isFailure get(): Boolean = response.run { this is BaseResponseModel && !success } // code != 200 }
    val isError get(): Boolean = response is Exception
    val isTransform get(): Boolean = !isSuccess && !isFailure && !isError

    suspend fun onSuccess(action: suspend (response: T) -> Unit): ResponseResult<T> {
        if (isSuccess) action(response as T)
        return this
    }

    suspend fun onFailure(action: suspend (BaseResponseModel) -> Unit): ResponseResult<T> {
        if (isFailure) action(response as BaseResponseModel)
        return this
    }

    suspend fun onError(action: suspend (Throwable) -> Unit): ResponseResult<T> {
        if (isError) action(response as Throwable)
        return this
    }

    companion object {
        fun <T> success(response: T): ResponseResult<T> = ResponseResult(response)
        fun <T> failure(baseResponseModel: BaseResponseModel): ResponseResult<T> = ResponseResult(baseResponseModel)
        fun <T> error(throwable: Throwable): ResponseResult<T> = ResponseResult(throwable)
    }
}

suspend inline fun <T, R : BaseResponseModel> T.makeRequest(noinline onResponse: suspend T.() -> R) : ResponseResult<R> {
    return try {
        withContext(Dispatchers.IO) {
            ResponseResult.success(onResponse(this@makeRequest))
        }
    } catch (exception: CancellationException) {
        println("jaehoLee", "CancellationException")
        ResponseResult.error(exception.fillInStackTrace())
    } catch (exception: Exception) {
        ResponseResult.error(exception.fillInStackTrace())
    }
}

fun <T> ResponseResult<T>.getSuccessOrNull() = when (isSuccess || isTransform) {
    true -> response as T
    false -> null
}

enum class ResponseResultType {
    Success,
    Failure,
    Error
}