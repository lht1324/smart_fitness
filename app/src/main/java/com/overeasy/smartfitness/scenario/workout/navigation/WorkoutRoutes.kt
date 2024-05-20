package com.overeasy.smartfitness.scenario.workout.navigation

import com.overeasy.smartfitness.scenario.setting.navigation.SettingRoutes

sealed class WorkoutRoutes(val route: String) {
    data object Workout : WorkoutRoutes("workout")
    data object Result : WorkoutRoutes(
        route = "result?" +
                "note_id={note_id}"
    ) {
        const val NOTE_ID = "note_id"

        fun createRoute(
            noteId: Int
        ) = route
            .replace("{${NOTE_ID}}", noteId.toString())
    }
}