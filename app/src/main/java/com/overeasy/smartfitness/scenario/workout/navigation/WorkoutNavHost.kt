package com.overeasy.smartfitness.scenario.workout.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.overeasy.smartfitness.scenario.public.Header
import com.overeasy.smartfitness.scenario.workout.result.WorkoutResultScreen
import com.overeasy.smartfitness.scenario.workout.workout.WorkoutScreen
import java.io.File

@Composable
fun WorkoutNavHost(
    modifier: Modifier = Modifier,
    filesDir: File?,
    onClickWatchExampleVideo: (String) -> Unit,
    onUpdateJson: (String) -> Unit,
    onChangeIsWorkoutRunning: (Boolean) -> Unit,
    onChangeHeaderHeight: (Int) -> Unit
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
            modifier = Modifier.onSizeChanged { (_, height) ->
                onChangeHeaderHeight(height)
            },
            title = currentHeaderTitle,
            onClickBack = {
                navHostController.navigateUp()
            },
            isBackButtonEnabled = currentDestination != WorkoutRoutes.Workout.route
        )
        NavHost(
            navController = navHostController,
            startDestination = WorkoutRoutes.Workout.route
        ) {
            composable(WorkoutRoutes.Workout.route) {
                WorkoutScreen(
                    filesDir = filesDir,
                    onClickWatchExampleVideo = onClickWatchExampleVideo,
                    onFinishWorkout = {
                        navHostController.navigate(WorkoutRoutes.Result.route)
                    },
                    onChangeIsWorkoutRunning = onChangeIsWorkoutRunning,
                    onUpdateJson = onUpdateJson
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