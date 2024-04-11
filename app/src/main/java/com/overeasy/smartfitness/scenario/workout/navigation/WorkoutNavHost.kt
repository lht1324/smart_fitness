package com.overeasy.smartfitness.scenario.workout.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.scenario.public.Header
import com.overeasy.smartfitness.scenario.workout.result.WorkoutResultScreen
import com.overeasy.smartfitness.scenario.workout.workout.WorkoutScreen

@Composable
fun WorkoutNavHost(
    modifier: Modifier = Modifier
) {
    val navHostController = rememberNavController()

    var currentDestination by remember { mutableStateOf(WorkoutRoutes.Workout.route) }
    val currentHeaderTitle by remember {
        derivedStateOf {
            when (currentDestination) {
                WorkoutRoutes.Workout.route -> "운동"
                WorkoutRoutes.Result.route -> "운동 결과"
                else -> ""
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        Header(
            title = currentHeaderTitle,
            isBackButtonEnabled = currentDestination != WorkoutRoutes.Workout.route
        )
        NavHost(
            navController = navHostController,
            startDestination = WorkoutRoutes.Workout.route
        ) {
            composable(WorkoutRoutes.Workout.route) {
                WorkoutScreen(
                    onClick = {
                        navHostController.navigate(WorkoutRoutes.Result.route)
                    }
                )
            }
            composable(WorkoutRoutes.Result.route) {
                WorkoutResultScreen()
            }
        }
    }

    LaunchedEffect(Unit) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination.route ?: WorkoutRoutes.Workout.route
        }
    }
}