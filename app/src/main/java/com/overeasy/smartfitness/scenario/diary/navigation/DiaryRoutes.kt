package com.overeasy.smartfitness.scenario.diary.navigation

sealed class DiaryRoutes(val route: String) {
    data object Diary : DiaryRoutes("diary")
    data object DiaryDetail : DiaryRoutes(
        route = "diary_detail?" +
                "note_id={note_id}"
    ) {
        const val NOTE_ID = "note_id"

        fun createRoute(
            date: String
        ) = route
            .replace("{${NOTE_ID}}", date)
    }
}