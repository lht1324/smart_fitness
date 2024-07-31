package com.overeasy.smartfitness.scenario.diary.navigation

sealed class DiaryRoutes(val route: String) {
    data object Diary : DiaryRoutes("diary")
    data object DiaryDetail : DiaryRoutes(
        route = "diary_detail?" +
                "note_id_list={note_id_list}&" +
                "date={date}"
    ) {
        const val NOTE_ID_LIST = "note_id_list"
        const val DATE = "date"

        fun createRoute(
            noteIdList: String,
            date: String
        ) = route
            .replace("{${NOTE_ID_LIST}}", noteIdList)
            .replace("{${DATE}}", date)
    }
}