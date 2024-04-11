package com.overeasy.smartfitness.scenario.diary.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.overeasy.smartfitness.scenario.diary.diary.DiaryScreen
import com.overeasy.smartfitness.scenario.diary.diarydetail.DiaryDetailScreen
import com.overeasy.smartfitness.scenario.public.Header
import java.time.LocalDate

@Composable
fun DiaryNavHost(
    modifier: Modifier = Modifier
) {
    val navHostController = rememberNavController()

    var currentDestination by remember { mutableStateOf(DiaryRoutes.Diary.route) }
    val currentHeaderTitle by remember {
        derivedStateOf {
            when (currentDestination) {
                DiaryRoutes.Diary.route -> "운동 일지"
                DiaryRoutes.DiaryDetail.route -> "상세 정보" // ${날짜} 상세 정보?
                else -> ""
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        Header(
            title = currentHeaderTitle,
            isBackButtonEnabled = currentDestination != DiaryRoutes.Diary.route
        )
        NavHost(
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

    LaunchedEffect(Unit) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination.route ?: DiaryRoutes.Diary.route
        }
    }
}