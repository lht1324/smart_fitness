package com.overeasy.smartfitness.domain.setting

import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.setting.dto.DeleteUsersRes
import com.overeasy.smartfitness.domain.setting.dto.GetUsersByIdRes
import com.overeasy.smartfitness.domain.setting.dto.GetUsersRes
import com.overeasy.smartfitness.domain.setting.dto.PostUsersLoginReq
import com.overeasy.smartfitness.domain.setting.dto.PostUsersLoginRes
import com.overeasy.smartfitness.domain.setting.dto.PostUsersSignUpReq
import com.overeasy.smartfitness.domain.setting.dto.PostUsersSignUpRes
import com.overeasy.smartfitness.domain.setting.dto.PutUsersReq

interface SettingRepository {
    suspend fun postUsersLogin(
        req: PostUsersLoginReq
    ): PostUsersLoginRes

    suspend fun postUsersSignUp(
        req: PostUsersSignUpReq
    ): PostUsersSignUpRes

    suspend fun getUsers(
        nickname: String, age: Int
    ): GetUsersRes

    suspend fun getUsersById(
        id: Int
    ): GetUsersByIdRes

    suspend fun putUsers(
        req: PutUsersReq
    ): BaseResponse

    suspend fun deleteUsers(
        id: Int
    ): DeleteUsersRes
}