package com.overeasy.smartfitness.domain.setting

import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginRes

interface SettingRepository {
    suspend fun postUsersLogin(
        id: String,
        password: String
    ): PostUsersLoginRes

    suspend fun postUsersSignup()
}