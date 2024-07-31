package com.overeasy.smartfitness.appConfig

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson

open class BaseAppPreference(
    private val context: Context,
    appPreferenceName: String
) {
    protected val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(appPreferenceName, Context.MODE_PRIVATE)

    protected fun setData(key: String, value: String?) {
        sharedPreferences.edit(true) { putString(key, value) }
    }

    protected fun setData(key: String, value: Int) {
        sharedPreferences.edit(true) { putInt(key, value) }
    }

    protected fun setData(key: String, value: Float) {
        sharedPreferences.edit(true) { putFloat(key, value) }
    }

    protected fun setData(key: String, value: Boolean) {
        sharedPreferences.edit(true) { putBoolean(key, value) }
    }

    protected fun setData(key: String, value: Any?) {
        sharedPreferences.edit(true) { putString(key, Gson().toJson(value)) }
    }

    protected fun getStringData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    protected fun getIntData(key: String, defaultValue: Int = Int.MIN_VALUE): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    protected fun getFloatData(key: String): Float {
        return sharedPreferences.getFloat(key, Float.MIN_VALUE)
    }

    protected fun getBooleanData(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    protected inline fun<reified T> getObjectData(key: String): T? {
        val json = sharedPreferences.getString(key, null)
        return try {
            Gson().fromJson(json, T::class.java)
        } catch (e: Exception) {
            null
        }
    }
}