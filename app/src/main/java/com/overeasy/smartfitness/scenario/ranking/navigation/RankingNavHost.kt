package com.overeasy.smartfitness.scenario.ranking.navigation

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
import com.overeasy.smartfitness.scenario.public.Header
import com.overeasy.smartfitness.scenario.ranking.ranking.RankingScreen

@Composable
fun RankingNavHost(
    modifier: Modifier = Modifier
) {
    val navHostController = rememberNavController()

    var currentDestination by remember { mutableStateOf(RankingRoutes.Ranking.route) }
    val currentHeaderTitle by remember {
        derivedStateOf {
            when (currentDestination) {
                RankingRoutes.Ranking.route -> "랭킹"
                else -> ""
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        Header(
            title = currentHeaderTitle,
            isBackButtonEnabled = currentDestination != RankingRoutes.Ranking.route
        )
        NavHost(
            navController = navHostController,
            startDestination = RankingRoutes.Ranking.route
        ) {
            composable(RankingRoutes.Ranking.route) {
                RankingScreen()
            }
        }
    }

    LaunchedEffect(Unit) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination.route ?: RankingRoutes.Ranking.route
        }
    }
}