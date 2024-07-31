package com.overeasy.smartfitness.scenario.workout.navigation

sealed class WorkoutRoutes(val route: String) {
    data object Workout : WorkoutRoutes("workout")
    data object Result : WorkoutRoutes(
        route = "result?" +
                "note_id={note_id}&" +
                "workout_name={workout_name}&" +
                "workout_result_index_list_string={workout_result_index_list_string}"
    ) {
        const val NOTE_ID = "note_id"
        const val WORKOUT_NAME = "workout_name"
        const val WORKOUT_RESULT_INDEX_LIST_STRING = "workout_result_index_list_string"

        fun createRoute(
            noteId: Int,
            workoutName: String,
            workoutResultIndexListString: String
        ) = route
            .replace("{${NOTE_ID}}", noteId.toString())
            .replace("{${WORKOUT_NAME}}", workoutName)
            .replace("{${WORKOUT_RESULT_INDEX_LIST_STRING}}", workoutResultIndexListString)
    }
}