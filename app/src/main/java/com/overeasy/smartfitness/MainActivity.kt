package com.overeasy.smartfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.model.ScreenState
import com.overeasy.smartfitness.scenario.diary.navigation.DiaryNavHost
import com.overeasy.smartfitness.scenario.diet.diet.DietScreen
import com.overeasy.smartfitness.scenario.diet.navigation.DietNavHost
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.public.Header
import com.overeasy.smartfitness.scenario.ranking.navigation.RankingNavHost
import com.overeasy.smartfitness.scenario.ranking.ranking.RankingScreen
import com.overeasy.smartfitness.scenario.setting.navigation.SettingNavHost
import com.overeasy.smartfitness.scenario.workout.navigation.WorkoutNavHost
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.SmartFitnessTheme
import com.overeasy.smartfitness.ui.theme.fontFamily
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isSystemInDarkTheme = isSystemInDarkTheme()
            var isShowFinishDialog by remember { mutableStateOf(false) }

            val pagerState = rememberPagerState(
                pageCount = {
                    ScreenState.entries.size
                }
            )

            val tabItemList = ScreenState.entries.map { state ->
                state.value
            }
            var currentPage by remember { mutableIntStateOf(2) }

            val screenHeight = LocalConfiguration.current.screenHeightDp
            var tabHeight by remember { mutableFloatStateOf(0f) }

            SmartFitnessTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    CurrentScreen(
                        modifier = Modifier.height(((screenHeight - tabHeight).dp)),
                        stateValue = tabItemList[currentPage]
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = ColorSecondary
                    )
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = ColorPrimary,
                        contentColor = ColorSecondary,
                        indicator = { _ ->
                            TabRowDefaults.Indicator(height = 1.dp, color = Color.Transparent)
                        }
                    ) {
                        tabItemList.forEachIndexed { index, tabItem ->
                            Row {
                                Tab(
                                    selected = currentPage == index,
                                    onClick = {
                                        currentPage = index
                                    },
                                    modifier = Modifier
                                        .border(
                                            width = 0.5.dp,
                                            color = ColorSecondary
                                        )
                                        .onSizeChanged { (_, height) ->
                                            val heightDp = pxToDp(height)

                                            if (heightDp != tabHeight)
                                                tabHeight = heightDp
                                        },
                                    text = {
                                        Text(
                                            text = tabItem,
                                            fontSize = 12.dpToSp(),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = fontFamily
                                        )
                                    },
                                    selectedContentColor = Color.White,
                                    unselectedContentColor = ColorSecondary
                                )
                            }
                        }
                    }
                }
            }

            BackHandler {
                isShowFinishDialog = !isShowFinishDialog
            }

            if (isShowFinishDialog) {
                Dialog(
//                    title = "끄려고요?",
//                    description = "운동은 하고 가는 거죠?",
                    title = "운동은 하고 가는 거죠?",
                    description = "켰다 끄는 건 좀 아니긴 한데...\n뭐라 하려는 건 아니에요 아 ㅋㅋ",
                    confirmText = "아니",
                    dismissText = "응",
                    onClickConfirm = {
                        isShowFinishDialog = false
                    },
                    onClickDismiss = {},
                    onDismissRequest = {
                        isShowFinishDialog = false
                    }
                )
            }

            LaunchedEffect(Unit) {
                enableEdgeToEdge(
                    statusBarStyle = if (isSystemInDarkTheme) {
                        SystemBarStyle.dark(ColorPrimary.toArgb())
                    } else {
                        SystemBarStyle.light(ColorPrimary.toArgb(), ColorPrimary.toArgb())
                    }
                )
            }
        }
    }
}

@Composable
fun CurrentScreen(
    modifier: Modifier = Modifier,
    stateValue: String
) = Box(
    modifier = modifier
) {
    when (stateValue) {
        ScreenState.DietScreen.value -> DietNavHost()
        ScreenState.DiaryScreen.value -> DiaryNavHost()
        ScreenState.MainScreen.value -> WorkoutNavHost()
        ScreenState.RankingScreen.value -> RankingNavHost()
        ScreenState.SettingScreen.value -> SettingNavHost()
        else -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = ColorPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "화면 로딩에 실패했습니다.",
                color = Color.White,
                fontSize = 18.dpToSp(),
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}