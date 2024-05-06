package com.overeasy.smartfitness.scenario.diary.diary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.domain.diary.model.Note
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.model.diary.CalendarItem
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.pxToDp
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryScreen(
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = hiltViewModel(),
    onClickMoveToDetail: (Int) -> Unit
) {
    val currentYear by viewModel.currentYear.collectAsState(initial = 1970)
    val currentMonth by viewModel.currentMonth.collectAsState(initial = 1)
    val calendarList by remember {
        derivedStateOf {
            viewModel.calendarList.toList()
        }
    }
    val calendarIndex by viewModel.calendarIndex.collectAsState()
    val selectedDiaryItem by viewModel.selectedDiaryItem.collectAsState()

    if (calendarIndex != null && calendarList.isNotEmpty()) {
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState(
            initialPage = calendarIndex!!,
            pageCount = {
                calendarList.size
            }
        )
        var selectedCalendarItem by remember { mutableStateOf<CalendarItem?>(null) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = ColorPrimary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Calendar(
                pagerState = pagerState,
                currentYear = currentYear,
                currentMonth = currentMonth,
                calendarList = calendarList,
                onChangeMonth = { isSwipeToLeft ->
                    coroutineScope.launch {
                        if (isSwipeToLeft) {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        } else {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                onClickItem = { calendarItem ->
                    calendarItem?.date?.let { date ->
                        viewModel.onClickCalendarItem(date)
                    }

                    selectedCalendarItem = calendarItem
                }
            )
            InfoSection(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .padding(horizontal = 24.dp),
                selectedDiaryItem = selectedDiaryItem,
                selectedCalendarItem = selectedCalendarItem,
                onClickMoveToDetail = onClickMoveToDetail
            )
        }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.map { page ->
                page to page
            }.scan(calendarIndex!! to calendarIndex!!) { previous, next ->
                previous.second to next.first
            }.filter { (previous, next) ->
                previous != next
            }.map { (previous, next) ->
                previous > next
            }.collectLatest { isSwipedToLeft ->
                selectedCalendarItem = null

                viewModel.onChangeMonth(isSwipedToLeft)
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = ColorPrimary)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            CircularProgressIndicator(
                modifier = Modifier.size(100.dp),
                color = ColorSaturday,
                strokeWidth = 15.dp
            )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "데이터를 불러오는 중입니다.\n잠시만 기다려주세요...",
                    color = ColorSecondary,
                    fontSize = 24.dpToSp(),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun InfoSection(
    modifier: Modifier = Modifier,
    selectedDiaryItem: Note?,
    selectedCalendarItem: CalendarItem?,
    onClickMoveToDetail: (Int) -> Unit
) {
    val context = LocalContext.current

    var textHeight by remember { mutableIntStateOf(0) }
    val textHeightDp by remember {
        derivedStateOf {
            context.pxToDp(textHeight)
        }
    }

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .padding(top = (20.0f + (textHeightDp / 2.0f)).dp)
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = AbsoluteRoundedCornerShape(5.dp)
                )
        )
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .background(
                    color = ColorPrimary
                )
                .align(Alignment.TopStart)
        ) {
            Text(
                text = if (selectedDiaryItem != null) {
                    val splitDate = selectedDiaryItem.workoutDate.split('-')
                    val year = splitDate[0].toInt()
                    val month = splitDate[1].toInt()
                    val day = splitDate[2].toInt()

                    selectedDiaryItem.run { "${year}년 ${month}월 ${day}일" }
                } else {
                    "운동 정보"
                },
                modifier = Modifier
                    .padding(20.dp)
                    .onSizeChanged { (_, height) ->
                        if (height != textHeight) {
                            textHeight = height
                        }
                    },
                color = Color.White,
                fontSize = 24.dpToSp(),
                fontWeight = FontWeight.ExtraBold,
                fontFamily = fontFamily
            )
        }
        Column(
            modifier = Modifier
                .padding(top = (40 + (textHeightDp / 2.0f)).dp)
                .padding(horizontal = 20.dp)
        ) {
            if (selectedDiaryItem != null) {
                Text(
                    text = "세트 총합",
                    color = Color.White,
                    fontSize = 20.dpToSp(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
                Text(
                    text = "${selectedDiaryItem.run { totalPerfect + totalGood + totalBad }}회",
                    color = Color.White,
                    fontSize = 18.dpToSp(),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fontFamily
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (selectedDiaryItem.totalKcal != null) {
                    Text(
                        text = "칼로리 섭취량",
                        color = Color.White,
                        fontSize = 20.dpToSp(),
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily
                    )
                    Text(
                        text = "${selectedDiaryItem.totalKcal} kcal",
                        color = Color.Red,
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fontFamily
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Text(
                    text = "칼로리 소모량",
                    color = Color.White,
                    fontSize = 20.dpToSp(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
                Text(
                    text = "${selectedDiaryItem.totalKcal} kcal", // 수정
                    color = Color(0xFF08E95F),
                    fontSize = 18.dpToSp(),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fontFamily
                )
            } else {
                Text(
                    text = "운동 정보가 존재하지 않아요.",
                    color = Color.White,
                    fontSize = 20.dpToSp(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
            }
        }
        if (selectedDiaryItem != null) {
            Box(
                modifier = Modifier
                    .padding(
                        top = (40 + (textHeightDp / 2.0f)).dp,
                        end = 20.dp
                    )
                    .background(
                        color = ColorSecondary,
                        shape = AbsoluteRoundedCornerShape(20.dp)
                    )
                    .noRippleClickable {
                        onClickMoveToDetail(selectedDiaryItem.noteId ?: -1)
                    }
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = "상세 정보",
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.Center),
                    color = Color.White,
                    fontSize = 16.dpToSp(),
                    fontWeight = FontWeight.Normal,
                    fontFamily = fontFamily
                )
            }
        }
    }

//    Box(
//        modifier = modifier
//    ) {
//        Box(
//            modifier = Modifier
//                .padding(top = (20.0f + (textHeightDp / 2.0f)).dp)
//                .fillMaxSize()
//                .border(
//                    width = 1.dp,
//                    color = Color.White,
//                    shape = AbsoluteRoundedCornerShape(5.dp)
//                )
//        )
//        Box(
//            modifier = Modifier
//                .padding(start = 10.dp)
//                .background(
//                    color = ColorPrimary
//                )
//                .align(Alignment.TopStart)
//        ) {
//            Text(
//                text = if (selectedCalendarItem != null) {
//                    val splitDate = selectedCalendarItem.date.split('-')
//                    val year = splitDate[0].toInt()
//                    val month = splitDate[1].toInt()
//                    val day = splitDate[2].toInt()
//
//                    selectedCalendarItem.run { "${year}년 ${month}월 ${day}일" }
//                } else {
//                    "운동 정보"
//                },
//                modifier = Modifier
//                    .padding(20.dp)
//                    .onSizeChanged { (_, height) ->
//                        if (height != textHeight) {
//                            textHeight = height
//                        }
//                    },
//                color = Color.White,
//                fontSize = 24.dpToSp(),
//                fontWeight = FontWeight.ExtraBold,
//                fontFamily = fontFamily
//            )
//        }
//        Column(
//            modifier = Modifier
//                .padding(top = (40 + (textHeightDp / 2.0f)).dp)
//                .padding(horizontal = 20.dp)
//        ) {
//            if (selectedCalendarItem != null) {
//                Text(
//                    text = "세트 총합",
//                    color = Color.White,
//                    fontSize = 20.dpToSp(),
//                    fontWeight = FontWeight.Bold,
//                    fontFamily = fontFamily
//                )
//                Text(
//                    text = "${selectedCalendarItem.daySetCount}회",
//                    color = Color.White,
//                    fontSize = 18.dpToSp(),
//                    fontWeight = FontWeight.SemiBold,
//                    fontFamily = fontFamily
//                )
//                Spacer(modifier = Modifier.height(10.dp))
//                if (selectedCalendarItem.dayCalorieIncome != null) {
//                    Text(
//                        text = "칼로리 섭취량",
//                        color = Color.White,
//                        fontSize = 20.dpToSp(),
//                        fontWeight = FontWeight.Bold,
//                        fontFamily = fontFamily
//                    )
//                    Text(
//                        text = "${selectedCalendarItem.dayCalorieIncome} kcal",
//                        color = Color.Red,
//                        fontSize = 18.dpToSp(),
//                        fontWeight = FontWeight.SemiBold,
//                        fontFamily = fontFamily
//                    )
//                    Spacer(modifier = Modifier.height(10.dp))
//                }
//                Text(
//                    text = "칼로리 소모량",
//                    color = Color.White,
//                    fontSize = 20.dpToSp(),
//                    fontWeight = FontWeight.Bold,
//                    fontFamily = fontFamily
//                )
//                Text(
//                    text = "${selectedCalendarItem.dayCalorieUsage} kcal",
//                    color = Color(0xFF08E95F),
//                    fontSize = 18.dpToSp(),
//                    fontWeight = FontWeight.SemiBold,
//                    fontFamily = fontFamily
//                )
//            } else {
//                Text(
//                    text = "운동 정보가 존재하지 않아요.",
//                    color = Color.White,
//                    fontSize = 20.dpToSp(),
//                    fontWeight = FontWeight.Bold,
//                    fontFamily = fontFamily
//                )
//            }
//        }
//        if (selectedCalendarItem != null) {
//            Box(
//                modifier = Modifier
//                    .padding(
//                        top = (40 + (textHeightDp / 2.0f)).dp,
//                        end = 20.dp
//                    )
//                    .background(
//                        color = ColorSecondary,
//                        shape = AbsoluteRoundedCornerShape(20.dp)
//                    )
//                    .noRippleClickable {
//                        onClickMoveToDetail(selectedCalendarItem.date)
//                    }
//                    .align(Alignment.TopEnd)
//            ) {
//                Text(
//                    text = "상세 정보",
//                    modifier = Modifier
//                        .padding(10.dp)
//                        .align(Alignment.Center),
//                    color = Color.White,
//                    fontSize = 16.dpToSp(),
//                    fontWeight = FontWeight.Normal,
//                    fontFamily = fontFamily
//                )
//            }
//        }
//    }
}