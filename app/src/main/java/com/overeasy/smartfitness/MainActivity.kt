package com.overeasy.smartfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.model.ScreenState
import com.overeasy.smartfitness.scenario.diary.diary.DiaryScreen
import com.overeasy.smartfitness.scenario.diary.navigation.DiaryNavHost
import com.overeasy.smartfitness.scenario.workout.workout.MainScreen
import com.overeasy.smartfitness.scenario.public.Header
import com.overeasy.smartfitness.scenario.ranking.RankingScreen
import com.overeasy.smartfitness.scenario.setting.SettingScreen
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily
import com.overeasy.smartfitness.ui.theme.SmartFitnessTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            val coroutineScope = rememberCoroutineScope()
            val pagerState = rememberPagerState(
                pageCount = {
                    ScreenState.entries.size
                }
            )

            val tabItemList = ScreenState.entries.map { state ->
                state.value
            }
            var currentPage by remember { mutableIntStateOf(0) }

            val screenHeight = LocalConfiguration.current.screenHeightDp
            var headerHeight by remember { mutableFloatStateOf(0f) }
            var tabHeight by remember { mutableFloatStateOf(0f) }
            val headerTitle by remember {
                derivedStateOf {
//                    tabItemList[pagerState.targetPage]
                    tabItemList[currentPage]
                }
            }

            SmartFitnessTheme {
                Column(Modifier.fillMaxSize()) {
                    Header(
                        modifier = Modifier.onSizeChanged { (_, height) ->
                            val heightDp = pxToDp(height)

                            if (heightDp != headerHeight)
                                headerHeight = heightDp
                        },
                        title = headerTitle
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = ColorSecondary
                    )
                    CurrentScreen(
                        modifier = Modifier.height(((screenHeight - (headerHeight + tabHeight)).dp)),
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
                                        Text(text = tabItem)
                                    },
                                    selectedContentColor = Color.White,
                                    unselectedContentColor = ColorSecondary
                                )
                            }
                        }
                    }
                }
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
        ScreenState.MainScreen.value -> MainScreen()
//        ScreenState.DiaryScreen.value -> DiaryScreen()
        ScreenState.DiaryScreen.value -> DiaryNavHost()
        ScreenState.RankingScreen.value -> RankingScreen()
        ScreenState.SettingScreen.value -> SettingScreen()
        else -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "화면 로딩에 실패했습니다.",
                fontSize = 18.dpToSp(),
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}