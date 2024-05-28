package com.overeasy.smartfitness.scenario.diet.navigation

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
import com.overeasy.smartfitness.scenario.diet.diet.DietScreen
import com.overeasy.smartfitness.scenario.diet.result.DietResultScreen
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.public.Header

@Composable
fun DietNavHost(
    modifier: Modifier = Modifier
) {
    val navHostController = rememberNavController()

    var currentDestination by remember { mutableStateOf(DietRoutes.Diet.route) }
    val currentHeaderTitle by remember {
        derivedStateOf {
            when (currentDestination) {
                DietRoutes.Diet.route -> "식단"
                DietRoutes.DietResult.route -> "식단 추천 결과"
                else -> ""
            }
        }
    }

    var isShowNotFinishDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Header(
            title = currentHeaderTitle,
//            isBackButtonEnabled = currentDestination != DietRoutes.Diet.route
            isBackButtonEnabled = false
        )
        NavHost(
            navController = navHostController,
            startDestination = DietRoutes.Diet.route
        ) {
            composable(DietRoutes.Diet.route) {
                DietScreen(
                    onFinish = { userMenu ->
                        navHostController.navigate(DietRoutes.DietResult.createRoute(userMenu))
                    }
                )
            }
            composable(DietRoutes.DietResult.route) { backStackEntry ->
                val userMenu = backStackEntry.arguments?.getString(DietRoutes.DietResult.USER_MENU) ?: ""

                DietResultScreen(
                    userMenu = userMenu,
                    onFinish = {
                        navHostController.navigateUp()
                    },
                    onFailureRecommend = {
                        isShowNotFinishDialog = true
                        navHostController.navigateUp()
                    }
                )
            }
        }
    }

    if (isShowNotFinishDialog) {
        Dialog(
            title = "제가 모르는 음식이네요.",
            description = "다시 한 번만 입력해주시겠어요?\n" +
                    "(빈 칸은 상관없어요 \uD83D\uDE09)",
            confirmText = "확인",
            onClickConfirm = {
                isShowNotFinishDialog = false
            },
            onDismissRequest = {
                isShowNotFinishDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination.route ?: DietRoutes.Diet.route
        }
    }
}