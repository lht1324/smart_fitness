package com.overeasy.smartfitness.scenario.diary

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.model.CalendarItem
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryScreen(
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val currentDate by viewModel.currentDate.collectAsState(initial = null)
    val calendarList by remember {
        derivedStateOf {
            viewModel.calendarList.toList()
        }
    }

    val pagerState = rememberPagerState(
        initialPage = 2,
        pageCount = {
            calendarList.size
        }
    )

    if (currentDate != null && calendarList.isNotEmpty()) {
        val pagerState = rememberPagerState(
            pageCount = {
                calendarList.size
            }
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = ColorPrimary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Calendar(
                pagerState = pagerState,
                currentDate = currentDate!!,
                calendarList = calendarList
            )
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = ColorPrimary)
        ) {
            // loading
        }
    }

    LaunchedEffect(pagerState.isScrollInProgress)  {
        println("jaehoLee", "isInProgress = ${pagerState.isScrollInProgress}")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Calendar(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    currentDate: LocalDate,
    calendarList: List<List<CalendarItem>>
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DayOfWeekRow()
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = ColorSecondary
        )
        HorizontalPager(state = pagerState) { page ->
            Column(
                modifier = Modifier.wrapContentHeight(),
            ) {
                val calendarRowCount = calendarList[page].size / 7

                for (i in 1..calendarRowCount) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row {
                        for (j in 1..7) {
                            CalendarItem(calendarItem = calendarList[page][(i - 1) * 7 + j - 1])
                            if (i in 1..6) {
                                Spacer(modifier = Modifier.width(5.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect { page ->
            // date 변경
        }
    }
}

@Composable
private fun CalendarHeader(
    onClickLeftButton: () -> Unit,
    onClickRightButton: () -> Unit
) {

}

@Composable
private fun DayOfWeekRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
//        horizontalArrangement = Arrangement.SpaceAround
    ) {
        DayOfWeekRowItem(text = "일")
        Spacer(modifier = Modifier.width(5.dp))
        DayOfWeekRowItem(text = "월")
        Spacer(modifier = Modifier.width(5.dp))
        DayOfWeekRowItem(text = "화")
        Spacer(modifier = Modifier.width(5.dp))
        DayOfWeekRowItem(text = "수")
        Spacer(modifier = Modifier.width(5.dp))
        DayOfWeekRowItem(text = "목")
        Spacer(modifier = Modifier.width(5.dp))
        DayOfWeekRowItem(text = "금")
        Spacer(modifier = Modifier.width(5.dp))
        DayOfWeekRowItem(text = "토")
    }
}

@Composable
private fun DayOfWeekRowItem(
    modifier: Modifier = Modifier,
    text: String
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Box(
        modifier = modifier
            .size(((screenWidth - (24 + 24)) / 7.0f).dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier
                .align(Alignment.Center),
            color = Color.White,
            fontSize = 12.dpToSp(),
            fontFamily = fontFamily
        )
    }
}

@Composable
private fun CalendarItem(
    modifier: Modifier = Modifier,
    calendarItem: CalendarItem
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val itemSize = (screenWidth - (24 + 24)) / 7.0f

    Box(
        modifier = modifier
            .size(itemSize.dp)
            .border(
                width = 2.dp,
                color = Color.White,
                shape = AbsoluteRoundedCornerShape(5.dp)
            )
    ) {
        val splitDate = calendarItem.date.split('/')

        Column(
            modifier = Modifier
//                .fillMaxSize()
                .padding(2.dp)
                .align(Alignment.TopEnd),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = splitDate[0],
                color = Color.White,
                fontSize = 11.dpToSp(),
                fontWeight = FontWeight.Light,
                fontFamily = fontFamily
            )
            Text(
                text = splitDate[1],
                color = Color.White,
                fontSize = 11.dpToSp(),
                fontWeight = FontWeight.Light,
                fontFamily = fontFamily
            )
            Text(
                text = splitDate[2],
                color = Color.White,
                fontSize = 11.dpToSp(),
                fontWeight = FontWeight.Light,
                fontFamily = fontFamily
            )
        }
        if (!calendarItem.isActive) {
            Box(
                modifier = Modifier
                    .size(itemSize.dp)
                    .background(color = Color.DarkGray.copy(alpha = 0.3f))
            )
        }
    }
}