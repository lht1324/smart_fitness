import com.android.build.api.dsl.AaptOptions

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.overeasy.smartfitness"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.overeasy.smartfitness"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "appId", "\"smart_fitness\"")
    }

    buildTypes {
        release {
            applicationIdSuffix = ".release"
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "IS_DEBUG", "false")
            buildConfigField("String", "BASE_URL", "\"https://namu.wiki\"")
        }

        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false

            buildConfigField("boolean", "IS_DEBUG", "true")
//            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080\"") // 에뮬
//            buildConfigField("String", "BASE_URL", "\"http://172.30.1.41:8080\"") // 집
//            buildConfigField("String", "BASE_URL", "\"http://172.16.230.236:8080\"") // 학교
            buildConfigField("String", "BASE_URL", "\"http://ceprj.gachon.ac.kr:60008\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    androidResources {
        noCompress.add("tflite")
    }
}

dependencies {
    // Google
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.multidex)
    implementation(libs.gson)
    implementation(libs.guava)

    // Kotlin
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlin.reflect)

    // DI
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.dagger)
    kapt(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.dagger.compiler)
    kapt(libs.dagger.android.processor)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.accompanist.permissions)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation.v2310)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.serialization.kotlinx.json)
//    implementation(libs.logback.classic)
//    testImplementation(libs.ktor.client.mock)

    // CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Google ML Kit
    implementation(libs.pose.detection)
    implementation(libs.pose.detection.accurate)

    // TensorFlow
    implementation(libs.tensorflow.lite)

    // Coil
    implementation(libs.coil.compose)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}