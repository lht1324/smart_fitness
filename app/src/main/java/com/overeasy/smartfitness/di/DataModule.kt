//package com.overeasy.smartfitness.di
//
//import android.os.Build
//import dagger.Provides
//import io.ktor.client.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.features.*
//import io.ktor.client.features.json.*
//import io.ktor.client.features.json.serializer.*
//import io.ktor.client.features.logging.*
//import io.ktor.client.request.*
//import io.ktor.http.*
//import io.ktor.client.plugins.HttpTimeout
//import javax.inject.Singleton
//
//object DataModule {
//    @Singleton
//    @Provides
//    fun provideKtorHttpClient(): HttpClient = HttpClient(CIO) {
//        val appVersion = BuildConfig.VERSION_NAME
//        val sdkVersion = Build.VERSION.SDK_INT
//
//        install(JsonFeature) {
//            serializer = KotlinxSerializer(
//                Json {
//                    prettyPrint = true
//                    isLenient = true
//                    ignoreUnknownKeys = true
//                }
//            )
//        }
//
//        install(HttpTimeout) {
//            requestTimeoutMillis = 30000L
//            connectTimeoutMillis = 30000L
//            socketTimeoutMillis = 30000L
//        }
//
////        HttpResponseValidator {
////            handleResponseException { throwable ->
////                SafeResponseException(
////                    throwable = throwable,
////                    userId = MainApp.appPref.userId.toString(),
////                    version = Version(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
////                    toaster = toaster,
////                    serverThrowableHelper = serverThrowableHelper
////                )
////            }
////        }
//
//        if (BuildConfig.DEBUG) {
//            install(Logging) {
//                logger = object : Logger {
//                    override fun log(message: String) {
//                        Timber.d(message)
//                    }
//                }
//                level = LogLevel.ALL
//            }
//        }
////        defaultRequest {
////            header("App-Id", appId)
////            header("App-Info", appInfo)
////            header("Access-Token", jwtNetwork.accessToken)
////            contentType(ContentType.Application.Json)
////        }
//    }
//}