package com.overeasy.smartfitness.di

import android.os.Build
import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.diet.impl.DietRepositoryImpl
import com.overeasy.smartfitness.domain.diary.DiaryRepository
import com.overeasy.smartfitness.domain.diary.impl.DiaryRepositoryImpl
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.impl.WorkoutRepositoryImpl
import com.overeasy.smartfitness.domain.ranking.RankingRepository
import com.overeasy.smartfitness.domain.ranking.impl.RankingRepositoryImpl
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.domain.setting.impl.SettingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Singleton
    @Provides
    fun provideKtorHttpClient(): HttpClient = HttpClient(CIO) {
        val appId = BuildConfig.appId
        val appVersion = BuildConfig.VERSION_NAME
        val sdkVersion = Build.VERSION.SDK_INT
        val appInfo = "AOS,$appVersion,$sdkVersion"

        install(Logging) {
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
            headers {
//                "App-Id" to appId
//                "App-Info" to appInfo
                HttpHeaders.ContentType to ContentType.Application.Json
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60000L
            connectTimeoutMillis = 60000L
            socketTimeoutMillis = 60000L
        }

        defaultRequest {
//            header("App-Id", appId)
//            header("App-Info", appInfo)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
//            contentType(ContentType.Application.Json)
        }
    }

    @Singleton
    @Provides
    fun provideDietRepository(client: HttpClient): DietRepository = DietRepositoryImpl(client)

    @Singleton
    @Provides
    fun provideDiaryRepository(client: HttpClient): DiaryRepository = DiaryRepositoryImpl(client)

    @Singleton
    @Provides
    fun provideWorkoutRepository(client: HttpClient): WorkoutRepository = WorkoutRepositoryImpl(client)

    @Singleton
    @Provides
    fun provideRankingRepository(client: HttpClient): RankingRepository = RankingRepositoryImpl(client)

    @Singleton
    @Provides
    fun provideSettingRepository(client: HttpClient): SettingRepository = SettingRepositoryImpl(client)
}