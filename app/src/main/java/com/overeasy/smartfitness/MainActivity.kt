package com.overeasy.smartfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.model.ScreenState
import com.overeasy.smartfitness.scenario.diary.DiaryScreen
import com.overeasy.smartfitness.scenario.main.MainScreen
import com.overeasy.smartfitness.scenario.ranking.RankingScreen
import com.overeasy.smartfitness.scenario.setting.SettingScreen
import com.overeasy.smartfitness.ui.theme.fontFamily
import com.overeasy.smartfitness.ui.theme.SmartFitnessTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()

            val screenHeight = LocalConfiguration.current.screenHeightDp
            var tabHeight by remember { mutableFloatStateOf(0f) }

            val tabItemList = ScreenState.entries.map { state ->
                state.value
            }
            val pagerState = rememberPagerState(
                pageCount = {
                    tabItemList.size
                }
            )
            SmartFitnessTheme {
                Column(Modifier.fillMaxSize()) {
                    HorizontalPager(
                        modifier = Modifier.height(((screenHeight - tabHeight).dp)),
                        state = pagerState,
                        userScrollEnabled = false
                    ) { page ->
                        CurrentScreen(stateValue = tabItemList[page])
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(color = Color.Blue)
                            .weight(1f)
                    )
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = Color.White,
                        contentColor = Color.Magenta,
                        indicator = { _ ->
                            TabRowDefaults.Indicator(height = 1.dp, color = Color.Transparent)
                        }
                    ) {
                        tabItemList.forEachIndexed { index, tabItem ->
                            Row {
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                    modifier = Modifier
                                        .border(
                                            width = 0.dp,
                                            color = Color.Black
                                        )
                                        .onSizeChanged { size ->
                                            val heightDp = pxToDp(size.height)

                                            if (heightDp != tabHeight)
                                                tabHeight = heightDp
                                        },
                                    text = {
                                        Text(text = tabItem)
                                    },
                                    selectedContentColor = Color.Blue,
                                    unselectedContentColor = Color.Cyan
                                )
//                                if (index != tabItemList.size - 1) {
//                                    Divider(
//                                        modifier = Modifier.size(
//                                            width = 2.dp,
//                                            height = tabHeight.dp
//                                        ).weight(1f),
//                                        color = Color.Black
//                                    )
//                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CurrentScreen(stateValue: String) = when (stateValue) {
    ScreenState.MainScreen.value -> MainScreen()
    ScreenState.DiaryScreen.value -> DiaryScreen()
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