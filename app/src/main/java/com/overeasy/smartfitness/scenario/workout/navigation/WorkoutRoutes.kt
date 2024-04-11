package com.overeasy.smartfitness.scenario.workout.navigation

import com.overeasy.smartfitness.scenario.setting.navigation.SettingRoutes

sealed class WorkoutRoutes(val route: String) {
    data object Workout : WorkoutRoutes("workout")
    data object Result : WorkoutRoutes("result")
}