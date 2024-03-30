package com.overeasy.smartfitness.scenario.diary.navigation

sealed class DiaryRoutes(val route: String) {
    data object Diary : DiaryRoutes("diary")
    data object DiaryDetail : DiaryRoutes(
        route = "diary_detail?" +
                "date={date}"
    ) {
        const val DATE = "date"

        fun createRoute(
            date: String
        ) = route
            .replace("{${DATE}}", date)
    }
}