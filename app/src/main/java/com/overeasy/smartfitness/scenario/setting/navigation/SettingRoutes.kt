package com.overeasy.smartfitness.scenario.setting.navigation

sealed class SettingRoutes(val route: String) {
    data object Setting : SettingRoutes("setting")
    data object MyInfo : SettingRoutes("myInfo")
    data object Login : SettingRoutes("login")
    data object Logout : SettingRoutes("logout")
    data object Register : SettingRoutes("register")
    data object NicknameInput : SettingRoutes("nickname")
    data object BodyInfoInput : SettingRoutes("bodyInfoInput")
    data object Withdraw : SettingRoutes("withdraw")
    data object Finish : SettingRoutes(
        route = "finish?" +
                "finishState={finishState}"
    ) {
        const val FINISH_STATE = "finishState"

        fun createRoute(
            finishState: String
        ) = route.replace("{${FINISH_STATE}}", finishState)
    }
}