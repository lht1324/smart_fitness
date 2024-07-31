package com.overeasy.smartfitness.scenario.ranking.navigation

import com.overeasy.smartfitness.scenario.setting.navigation.SettingRoutes

sealed class RankingRoutes(val route: String) {
    data object Ranking : RankingRoutes("ranking")
}