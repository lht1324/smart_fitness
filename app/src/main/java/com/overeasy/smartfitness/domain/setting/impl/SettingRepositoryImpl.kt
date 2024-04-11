package com.overeasy.smartfitness.domain.setting.impl

import com.overeasy.smartfitness.domain.setting.SettingRepository
import io.ktor.client.HttpClient
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : SettingRepository {

}