package com.overeasy.smartfitness.scenario.setting.navigation

sealed class SettingRoutes(val route: String) {
    data object Setting : SettingRoutes("setting")
    data object Login : SettingRoutes("login")
    data object FindId : SettingRoutes("findId")
    data object Register : SettingRoutes("register")
    data object MyInfo : SettingRoutes("myInfo")
    data object Logout : SettingRoutes("logout")
    data object Withdraw : SettingRoutes("withdraw")
    data object Finish : SettingRoutes("finish")
}