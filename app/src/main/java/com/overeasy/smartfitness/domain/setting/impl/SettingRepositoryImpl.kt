@file:OptIn(InternalAPI::class)

package com.overeasy.smartfitness.domain.setting.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.domain.setting.entity.DeleteUsersRes
import com.overeasy.smartfitness.domain.setting.entity.GetUsersByIdRes
import com.overeasy.smartfitness.domain.setting.entity.GetUsersRes
import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginReq
import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginRes
import com.overeasy.smartfitness.domain.setting.entity.PostUsersSignUpReq
import com.overeasy.smartfitness.domain.setting.entity.PostUsersSignUpRes
import com.overeasy.smartfitness.domain.setting.entity.PutUsersReq
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.simpleDelete
import com.overeasy.smartfitness.simpleGet
import com.overeasy.smartfitness.simplePost
import com.overeasy.smartfitness.simplePut
import io.ktor.client.HttpClient
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : SettingRepository {
    private val baseUrl = BuildConfig.BASE_URL

    override suspend fun postUsersLogin(req: PostUsersLoginReq): PostUsersLoginRes =
        client.simplePost<PostUsersLoginRes>("$baseUrl/users/login") {
            body = Json.encodeToString(req)
        }

    override suspend fun postUsersSignUp(req: PostUsersSignUpReq): PostUsersSignUpRes =
        client.simplePost("$baseUrl/users/signup") {
            body = Json.encodeToString(req)
        }

    // logout 없음 (로컬 처리)

    override suspend fun getUsers(nickname: String, age: Int): GetUsersRes =
//        client.simpleGet("$baseUrl/users?nickname=\"$nickname\"&age=$age") {
        client.simpleGet("$baseUrl/users/${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(nickname, "UTF-8")
            }
        }/$age") {

        }

    override suspend fun getUsersById(id: Int): GetUsersByIdRes =
        client.simpleGet("$baseUrl/users/$id") {

        }

    override suspend fun putUsers(req: PutUsersReq): BaseResponse =
        client.simplePut("$baseUrl/users") {
            body = Json.encodeToString(req)
        }

    override suspend fun deleteUsers(id: Int): DeleteUsersRes =
        client.simpleDelete("$baseUrl/users/$id") {

        }

    @Serializable
    private data class Temp(
        val nickname: String,
        val age: Int
    )
}