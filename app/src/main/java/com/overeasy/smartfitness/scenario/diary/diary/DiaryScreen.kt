package com.overeasy.smartfitness.scenario.diary.diary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.model.diary.CalendarItemData
import com.overeasy.smartfitness.model.diary.ScoreType
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.pxToDp
import com.overeasy.smartfitness.ui.theme.ColorLightGreen
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.ColorSunday
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
    onClickMoveToDetail: (Int, String) -> Unit
) {
    val currentYear by viewModel.currentYear.collectAsState(initial = 1970)
    val currentMonth by viewModel.currentMonth.collectAsState(initial = 1)
    val calendarList = remember { viewModel.calendarList }
    val calendarIndex by viewModel.calendarIndex.collectAsState()

    val noteIdList by viewModel.noteIdList.collectAsState()

    val perfectCount by viewModel.prefectCount.collectAsState()
    val goodCount by viewModel.goodCount.collectAsState()
    val notGoodCount by viewModel.notGoodCount.collectAsState()
    val totalScore by viewModel.totalScore.collectAsState()
    val totalKcal by viewModel.totalKcal.collectAsState()

    if (calendarIndex != null && calendarList.isNotEmpty()) {
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState(
            initialPage = calendarIndex!!,
            pageCount = {
                calendarList.size
            }
        )
        val selectedCalendarItemData by viewModel.selectedCalendarItemData.collectAsState()

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
                selectedCalendarItemData = selectedCalendarItemData,
                onChangeMonth = { isSwipeToLeft ->
                    coroutineScope.launch {
                        if (isSwipeToLeft) {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        } else {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                onClickItem = { calendarData ->
                    calendarData?.date?.let { date ->
                        viewModel.onClickCalendarItem(date)
                    } ?: viewModel.clearDiaryData()

                    viewModel.onChangeSelectedCalendarData(calendarData)
                }
            )
            InfoSection(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 20.dp),
                noteIdList = noteIdList,
                perfectCount = perfectCount,
                goodCount = goodCount,
                notGoodCount = notGoodCount,
                totalScore = totalScore,
                totalKcal = totalKcal,
                selectedCalendarItemData = selectedCalendarItemData,
                onClickMoveToDetail = { noteId ->
                    if (selectedCalendarItemData != null) {
                        onClickMoveToDetail(noteId, selectedCalendarItemData!!.date)
                    }
                }
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
                viewModel.onChangeSelectedCalendarData(null)
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
                    color = Color.White,
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
    noteIdList: List<Int>,
    perfectCount: Int,
    goodCount: Int,
    notGoodCount: Int,
    totalScore: Int,
    totalKcal: Int,
    selectedCalendarItemData: CalendarItemData?,
    onClickMoveToDetail: (Int) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var textHeight by remember { mutableIntStateOf(0) }
    val textHeightDp by remember {
        derivedStateOf {
            context.pxToDp(textHeight)
        }
    }

    var isNotExistWorkoutInfo by remember { mutableStateOf(false) }

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
                text = if (selectedCalendarItemData != null) {
                    val splitDate = selectedCalendarItemData.date.split('-')
                    val year = splitDate[0].toInt()
                    val month = splitDate[1].toInt()
                    val day = splitDate[2].toInt()

                    "${year}년 ${month}월 ${day}일"
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
                .padding(top = (40 + (textHeightDp / 2.0f)).dp, bottom = 20.dp)
                .padding(horizontal = 20.dp)
                .verticalScroll(state = scrollState)
        ) {
            if (!isNotExistWorkoutInfo) {
                Text(
                    text = "총 운동 횟수",
                    color = Color.White,
                    fontSize = 20.dpToSp(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "${perfectCount + goodCount + notGoodCount}회",
                    color = Color.White,
                    fontSize = 18.dpToSp(),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fontFamily
                )
                if (!(perfectCount == 0 && goodCount == 0 && notGoodCount == 0)) {
                    Separator()
                    Text(
                        text = "점수",
                        color = Color.White,
                        fontSize = 20.dpToSp(),
                        fontWeight = FontWeight.Black,
                        fontFamily = fontFamily
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    ScoreCount(scoreType = ScoreType.PERFECT, scoreCount = perfectCount)
                    Spacer(modifier = Modifier.height(5.dp))
                    ScoreCount(scoreType = ScoreType.GOOD, scoreCount = goodCount)
                    Spacer(modifier = Modifier.height(5.dp))
                    ScoreCount(scoreType = ScoreType.NOT_GOOD, scoreCount = notGoodCount)
                }
                if (totalKcal != 0) {
                    Separator()
                    Text(
                        text = "칼로리 소모량",
                        color = Color.White,
                        fontSize = 20.dpToSp(),
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "$totalKcal kcal", // 수정
                        color = ColorLightGreen,
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fontFamily
                    )
                }
                if (noteIdList.isNotEmpty()) {
                    Separator()
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        noteIdList.forEachIndexed { index, noteId ->
                            Text(
                                text = "${index + 1}번째 운동 상세 정보 확인하기",
                                modifier = Modifier.noRippleClickable {
                                    onClickMoveToDetail(noteId)
                                },
                                color = ColorSaturday,
                                fontSize = 18.dpToSp(),
                                fontWeight = FontWeight.Medium,
                                fontFamily = fontFamily,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                }
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
        LaunchedEffect(
            perfectCount,
            goodCount,
            notGoodCount,
            totalScore,
            totalKcal
        ) {
            isNotExistWorkoutInfo = perfectCount == -1 &&
                    goodCount == -1 &&
                    notGoodCount == -1 &&
                    totalScore == -1 &&
                    totalKcal == 0
        }
    }
}

@Composable
private fun ScoreCount(
    modifier: Modifier = Modifier,
    scoreType: ScoreType,
    scoreCount: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (scoreType) {
                ScoreType.PERFECT -> "Perfect"
                ScoreType.GOOD -> "Good"
                ScoreType.NOT_GOOD -> "Not Good"
            },
            color = when (scoreType) {
                ScoreType.PERFECT -> ColorSaturday
                ScoreType.GOOD -> ColorLightGreen
                ScoreType.NOT_GOOD -> ColorSunday
            },
            fontSize = 16.dpToSp(),
            fontWeight = FontWeight.Black,
            fontFamily = fontFamily
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .width(5.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )
        Text(
            text = "$scoreCount",
            color = Color.LightGray,
            fontSize = 16.dpToSp(),
            fontWeight = FontWeight.SemiBold,
            fontFamily = fontFamily
        )
    }
}

@Composable
private fun Separator(
    modifier: Modifier = Modifier,
    spaceHeight: Int = 20
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height((spaceHeight / 2).dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height((spaceHeight / 2).dp))
    }
}