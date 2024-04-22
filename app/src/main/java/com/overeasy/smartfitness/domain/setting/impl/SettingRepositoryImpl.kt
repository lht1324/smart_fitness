@file:OptIn(InternalAPI::class)

package com.overeasy.smartfitness.domain.setting.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.simplePost
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginReq
import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginRes
import com.overeasy.smartfitness.domain.setting.entity.PostUsersSignUpReq
import com.overeasy.smartfitness.domain.setting.entity.PostUsersSignUpRes
import io.ktor.client.HttpClient
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
        client.simplePost("$baseUrl/users/login") {
            body = Json.encodeToString(req)
        }
}