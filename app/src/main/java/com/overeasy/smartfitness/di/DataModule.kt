package com.overeasy.smartfitness.di

import android.content.Context
import android.os.Build
import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.domain.ai.AiRepository
import com.overeasy.smartfitness.domain.ai.impl.AiRepositoryImpl
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.diet.impl.DietRepositoryImpl
import com.overeasy.smartfitness.domain.exercises.ExercisesRepository
import com.overeasy.smartfitness.domain.exercises.impl.ExercisesRepositoryImpl
import com.overeasy.smartfitness.domain.foods.FoodsRepository
import com.overeasy.smartfitness.domain.foods.impl.FoodsRepositoryImpl
import com.overeasy.smartfitness.domain.score.ScoreRepository
import com.overeasy.smartfitness.domain.score.impl.ScoreRepositoryImpl
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.domain.setting.impl.SettingRepositoryImpl
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.impl.WorkoutRepositoryImpl
import com.overeasy.smartfitness.module.tensorflowmanager.TensorFlowManager
import com.overeasy.smartfitness.module.tensorflowmanager.TensorFlowManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
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
//                HttpHeaders.AcceptCharset to Charsets.UTF_8
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000L
            connectTimeoutMillis = 30000L
            socketTimeoutMillis = 30000L
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
    fun provideTensorFlowManager(@ApplicationContext context: Context): TensorFlowManager = TensorFlowManagerImpl(context)

    @Singleton
    @Provides
    fun provideDietRepository(client: HttpClient): DietRepository = DietRepositoryImpl(client)

    @Singleton
    @Provides
    fun provideWorkoutRepository(client: HttpClient): WorkoutRepository = WorkoutRepositoryImpl(client)

    @Singleton
    @Provides
    fun provideScoreRepository(client: HttpClient): ScoreRepository = ScoreRepositoryImpl(client)

    @Singleton
    @Provides
    fun provideSettingRepository(client: HttpClient): SettingRepository = SettingRepositoryImpl(client)

    @Singleton
    @Provides
    fun provideExercisesRepository(client: HttpClient): ExercisesRepository = ExercisesRepositoryImpl(client)

    @Singleton
    @Provides
    fun provideAiRepository(client: HttpClient): AiRepository = AiRepositoryImpl(client)

    @Singleton
    @Provides
    fun provideFoodsRepository(client: HttpClient): FoodsRepository = FoodsRepositoryImpl(client)
}