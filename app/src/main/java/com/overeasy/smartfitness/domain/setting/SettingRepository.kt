package com.overeasy.smartfitness.domain.setting

import com.overeasy.smartfitness.domain.base.BaseResponse
import com.overeasy.smartfitness.domain.setting.entity.DeleteUsersRes
import com.overeasy.smartfitness.domain.setting.entity.GetUsersByIdRes
import com.overeasy.smartfitness.domain.setting.entity.GetUsersReq
import com.overeasy.smartfitness.domain.setting.entity.GetUsersRes
import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginReq
import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginRes
import com.overeasy.smartfitness.domain.setting.entity.PostUsersSignUpReq
import com.overeasy.smartfitness.domain.setting.entity.PostUsersSignUpRes
import com.overeasy.smartfitness.domain.setting.entity.PutUsersReq

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