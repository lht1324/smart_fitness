package com.overeasy.smartfitness.domain.setting.impl

import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.simplePost
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.domain.setting.entity.GetUsersLoginReq
import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginRes
import com.overeasy.smartfitness.println
import io.ktor.client.HttpClient
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : SettingRepository {
    private val baseUrl = BuildConfig.BASE_URL

    @OptIn(InternalAPI::class)
    override suspend fun postUsersLogin(id: String, password: String): PostUsersLoginRes =
        client.simplePost<PostUsersLoginRes>("$baseUrl/users/login") {
            body = Json.encodeToString(
                GetUsersLoginReq(
                    username = id,
                    password = password
                )
            )
        }

    override suspend fun postUsersSignup() {
        TODO("Not yet implemented")
    }
}