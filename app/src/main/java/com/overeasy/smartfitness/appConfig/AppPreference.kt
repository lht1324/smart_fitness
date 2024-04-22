package com.overeasy.smartfitness.appConfig

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreference @Inject constructor(
    @ApplicationContext context: Context,
    appPreferenceName: String = "appPref_simple_music_player"
) : BaseAppPreference(context, appPreferenceName) {
    companion object {
        private const val KEY_IS_LOGIN = "KEY_IS_LOGIN"
        private const val KEY_USER_ID = "KEY_USER_ID"
    }

    var isLogin: Boolean
        get() = getBooleanData(KEY_IS_LOGIN, false)
        set(value) {
            setData(KEY_IS_LOGIN, value)
        }

    var userId: Int
        get() = getIntData(KEY_USER_ID, -1)
        set(value) {
            setData(KEY_USER_ID, value)
        }
}