package com.overeasy.smartfitness.domain.setting

import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginReq
import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginRes
import com.overeasy.smartfitness.domain.setting.entity.PostUsersSignUpReq
import com.overeasy.smartfitness.domain.setting.entity.PostUsersSignUpRes

interface SettingRepository {
    suspend fun postUsersLogin(
        req: PostUsersLoginReq
    ): PostUsersLoginRes

    suspend fun postUsersSignUp(
        req: PostUsersSignUpReq
    ): PostUsersSignUpRes
}