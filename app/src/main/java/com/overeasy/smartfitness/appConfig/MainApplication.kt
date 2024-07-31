package com.overeasy.smartfitness.appConfig

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    companion object {
        lateinit var appPreference: AppPreference
    }

    override fun onCreate() {
        super.onCreate()
        appPreference = AppPreference(this)
    }
}