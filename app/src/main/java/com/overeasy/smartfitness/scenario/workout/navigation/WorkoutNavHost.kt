package com.overeasy.smartfitness.scenario.workout.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.scenario.diary.diarydetail.DiaryDetailScreen
import com.overeasy.smartfitness.scenario.public.Header
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
                    onFinishWorkout = { noteId ->
                        println("jaehoLee", "noteId onFinish = $noteId")
                        navHostController.navigate(WorkoutRoutes.Result.createRoute(noteId))
                    },
                    onChangeIsWorkoutRunning = onChangeIsWorkoutRunning,
                    onUpdateJson = onUpdateJson
                )
            }
            composable(WorkoutRoutes.Result.route) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString(WorkoutRoutes.Result.NOTE_ID)?.toIntOrNull() ?: -1

                if (noteId != -1) {
                    DiaryDetailScreen(
                        noteId = noteId,
                        isCameFromWorkout = true,
                        onClickWatchExampleVideo = onClickWatchExampleVideo
                    )
                } else {
                    navHostController.navigateUp()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination.route ?: WorkoutRoutes.Workout.route
        }
    }
}