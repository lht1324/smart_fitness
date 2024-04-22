package com.overeasy.smartfitness.scenario.diet.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.overeasy.smartfitness.scenario.diet.diet.DietScreen
import com.overeasy.smartfitness.scenario.diet.diet.FoodCategory
import com.overeasy.smartfitness.scenario.diet.result.DietResultScreen
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

    var selectedFoodCategory by remember { mutableStateOf(FoodCategory.KOREAN) }

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
                    onClickCategoryItem = { foodCategory ->
                        selectedFoodCategory = foodCategory
                        navHostController.navigate(DietRoutes.DietResult.route)
                    }
                )
            }
            composable(DietRoutes.DietResult.route) {
                DietResultScreen(
                    foodCategory = selectedFoodCategory,
                    onFinish = {
                        navHostController.navigateUp()
                    }
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination.route ?: DietRoutes.Diet.route
        }
    }
}