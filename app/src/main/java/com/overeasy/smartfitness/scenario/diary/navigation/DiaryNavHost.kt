package com.overeasy.smartfitness.scenario.diary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.overeasy.smartfitness.scenario.diary.diary.DiaryScreen
import com.overeasy.smartfitness.scenario.diary.diarydetail.DiaryDetailScreen
import java.time.LocalDate

@Composable
fun DiaryNavHost(
    modifier: Modifier = Modifier
) {
    val navHostController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = DiaryRoutes.Diary.route
    ) {
        composable(DiaryRoutes.Diary.route) {
            DiaryScreen(
                onClickMoveToDetail = { date ->
                    navHostController.navigate(DiaryRoutes.DiaryDetail.createRoute(date))
                }
            )
        }
        composable(DiaryRoutes.DiaryDetail.route) { backStackEntry ->
            DiaryDetailScreen(
                date = backStackEntry.arguments?.getString(DiaryRoutes.DiaryDetail.DATE)
                    ?: LocalDate.now().run { "$year-${String.format("%02d", monthValue)}-${String.format("%02d", dayOfMonth)}" }
            )
        }
    }
}