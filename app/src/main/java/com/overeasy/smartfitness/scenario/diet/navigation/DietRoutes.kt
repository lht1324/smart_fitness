package com.overeasy.smartfitness.scenario.diet.navigation

import com.overeasy.smartfitness.scenario.setting.navigation.SettingRoutes

sealed class DietRoutes(val route: String) {
    data object Diet : DietRoutes("diet")
    data object DietResult : DietRoutes("result")
}