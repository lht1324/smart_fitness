@file:OptIn(ExperimentalLayoutApi::class)

package com.overeasy.smartfitness.scenario.diary.diarydetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.R
import com.overeasy.smartfitness.addCommaIntoNumber
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.model.diary.ScoreType
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.pxToDp
import com.overeasy.smartfitness.ui.theme.ColorLightGreen
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.ColorSunday
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun DiaryDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DiaryDetailViewModel = hiltViewModel(),
    noteId: Int,
    noteDate: String? = null,
    onClickWatchExampleVideo: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    val diaryDetail by viewModel.diaryDetail.collectAsState()
    val workoutVideoDataList = remember { viewModel.workoutVideoDataList }
    val dietHistoryList = remember { viewModel.dietHistoryList }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        if (diaryDetail != null) {
            val workoutNameList by remember {
                derivedStateOf {
                    diaryDetail!!.workoutList.distinctBy { workoutInfo ->
                        workoutInfo.exerciseName
                    }.map { workoutInfo ->
                        workoutInfo.exerciseName
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        state = scrollState
                    )
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                ResultArea(
                    title = "운동",
                    contents = {
                        workoutNameList.forEachIndexed { index, workoutName ->
                            WorkoutSection(
                                name = workoutName,
                                workoutList = diaryDetail!!.workoutList.filter { workoutInfo ->
                                    workoutInfo.exerciseName == workoutName
                                }.sortedBy { workoutInfo ->
                                    workoutInfo.setNum
                                }.map { workoutInfo ->
                                    workoutInfo.run { setNum to repeats }
                                },
                                caloriePerEachSet = index + 5
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            if (index != workoutNameList.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 2.dp,
                                    color = ColorSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        Separator()
                        DetailText(
                            text = "점수", // sumOf
                            fontSize = 18.dpToSp()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ScoreSection(
                            type = ScoreType.PERFECT,
                            score = diaryDetail!!.totalPerfect
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ScoreSection(
                            type = ScoreType.GOOD,
                            score = diaryDetail!!.totalGood
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ScoreSection(
                            type = ScoreType.NOT_GOOD,
                            score = diaryDetail!!.totalBad
                        )
                        Separator()
                        DetailText(
                            text = "총점", // sumOf
                            fontSize = 20.dpToSp(),
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        DetailText(
                            text = "${addCommaIntoNumber(diaryDetail!!.totalScore)}점",
                            fontSize = 18.dpToSp(),
                            fontWeight = FontWeight.Black
                        )
                        if (workoutVideoDataList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 2.dp,
                                color = ColorSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            DetailText(
                                text = "운동 영상 다시 보기",
                                fontSize = 20.dpToSp(),
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            workoutVideoDataList.forEachIndexed { workoutIndex, dataList ->
                                dataList.forEachIndexed { index, (workoutName, url) ->
                                    DetailText(
                                        text = "$workoutName(${index + 1})",
                                        modifier = Modifier.noRippleClickable {
                                            com.overeasy.smartfitness.println("jaehoLee", "clicked url")
                                            onClickWatchExampleVideo(url)
                                        },
                                        fontSize = 18.dpToSp(),
                                        color = ColorSaturday,
                                        textDecoration = TextDecoration.Underline
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    if (index == dataList.size - 1 && workoutIndex != workoutVideoDataList.size - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier.fillMaxWidth(),
                                            thickness = 1.dp,
                                            color = Color.LightGray
                                        )
                                        Spacer(modifier = Modifier.height(5.dp))
                                    }
                                }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                ResultArea(
                    title = "식단",
                    contents = {
                        if (dietHistoryList.isNotEmpty()) {
                            Text(
                                text = "추천 결과",
                                color = Color.White,
                                fontSize = 20.dpToSp(),
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = fontFamily
                            )
                            Separator()
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                dietHistoryList.forEach { (foodName, calorie, foodCount) ->
                                    FoodInfo(
                                        foodName = foodName,
                                        calorie = calorie,
                                        foodCount = foodCount
                                    )
                                }
                            }
                            Separator()
                            Text(
                                text = "총 섭취 칼로리",
                                color = Color.White,
                                fontSize = 20.dpToSp(),
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = fontFamily
                            )
                            Separator()
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                dietHistoryList.map { (_, calorie, foodCount) ->
//                                    "${String.format("%.2f", calorie)} × $foodCount"
                                    "${calorie * foodCount.toFloat()}"
                                }.forEachIndexed { index, calorieUsage ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "+",
                                            color = if (index != 0) {
                                                Color.White
                                            } else {
                                                Color.Transparent
                                            },
                                            fontSize = 16.dpToSp(),
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = fontFamily
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = calorieUsage,
                                            color = Color.White,
                                            fontSize = 16.dpToSp(),
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = fontFamily
                                        )
                                    }
                                }
                            }
                            Separator()
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "=",
                                    color = Color.White,
                                    fontSize = 16.dpToSp(),
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = fontFamily
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${
                                        dietHistoryList.sumOf { (_, calorie, foodCount) ->
                                            (calorie * foodCount.toFloat()).toDouble()
                                        }.toFloat()
                                    } kcal",
                                    color = Color.White,
                                    fontSize = 18.dpToSp(),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = fontFamily
                                )
                            }
                        } else {
                            Text(
                                text = "식단을 추천받지 않았어요.",
                                modifier = Modifier
                                    .padding(vertical = 20.dp)
                                    .align(Alignment.CenterHorizontally),
                                color = Color.White,
                                fontSize = 20.dpToSp(),
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = fontFamily
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp),
                    color = ColorSaturday,
                    strokeWidth = 15.dp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "상세 정보를 조회 중입니다.\n잠시만 기다려주세요...",
                    color = Color.White,
                    fontSize = 24.dpToSp(),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        println("jaehoLee", "noteId = $noteId")
        viewModel.onLoad(noteId, noteDate)
    }
}

@Composable
private fun DetailText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.White,
    fontSize: TextUnit = 16.dpToSp(),
    fontWeight: FontWeight = FontWeight.Bold,
    textDecoration: TextDecoration = TextDecoration.None
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textDecoration = textDecoration
    )
}

@Composable
private fun ResultArea(
    modifier: Modifier = Modifier,
    contentsVerticalPadding: Dp = 20.dp,
    contentsHorizontalPadding: Dp = 20.dp,
    title: String,
    contents: @Composable ColumnScope.() -> Unit,
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
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(top = (textHeightDp / 2.0f).dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = ColorSecondary,
                    shape = AbsoluteRoundedCornerShape(5.dp)
                )
                .align(Alignment.TopStart)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = contentsVerticalPadding)
                    .padding(horizontal = contentsHorizontalPadding)
            ) {
                contents()
            }
        }
        Box(
            modifier = Modifier
                .padding(start = (24 + 20).dp)
                .background(
                    color = ColorPrimary
                )
                .onSizeChanged { (_, height) ->
                    if (height != textHeight) {
                        textHeight = height
                    }
                }
                .align(Alignment.TopStart)
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(vertical = 5.dp, horizontal = 10.dp),
                color = Color.White,
                fontSize = 24.dpToSp(),
                fontWeight = FontWeight.ExtraBold,
                fontFamily = fontFamily
            )
        }
    }
}

@Composable
private fun WorkoutSection(
    modifier: Modifier = Modifier,
    name: String,
    caloriePerEachSet: Int,
    workoutList: List<Pair<Int, Int>>
) {
    val context = LocalContext.current

    var isClicked by remember { mutableStateOf(false) }

    var dividerHeight by remember { mutableIntStateOf(0) }
    val dividerHeightDp by remember {
        derivedStateOf {
            context.pxToDp(dividerHeight)
        }
    }
    var maxIngredientTextWidth by remember { mutableIntStateOf(0) }
    val maxIngredientTextWidthDp by remember {
        derivedStateOf {
            context.pxToDp(maxIngredientTextWidth)
        }
    }

    val degree by animateFloatAsState(
        targetValue = if (isClicked) 180f else 0f,
        animationSpec = tween(
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    Column(
        modifier = modifier.noRippleClickable {
            isClicked = !isClicked
        }
    ) {
        DetailText(
            modifier = Modifier.onSizeChanged { (_, height) ->
                if (dividerHeight != height) {
                    dividerHeight = height
                }
            },
            text = "$name (1회 $caloriePerEachSet kcal 소모)",
            fontSize = 18.dpToSp()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailText(text = "세트 정보")
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_arrrow_down),
                modifier = Modifier
                    .size(16.dp)
                    .rotate(degree),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedVisibility(
            visible = isClicked
        ) {
            Column {
                workoutList.forEachIndexed { index, (set, count) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DetailText(
                            modifier = Modifier
                                .onSizeChanged { (width, _) ->
                                    if (width > maxIngredientTextWidth) {
                                        maxIngredientTextWidth = width
                                    }
                                }
                                .then(
                                    if (maxIngredientTextWidth > 0) {
                                        Modifier.width(maxIngredientTextWidthDp.dp)
                                    } else {
                                        Modifier
                                    }
                                ),
                            text = "${set}세트",
                            fontSize = 14.dpToSp()
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Divider(
                            modifier = Modifier
                                .width(5.dp)
                                .height(2.dp),
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        DetailText(
                            text = "${count}회",
                            fontSize = 14.dpToSp()
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        DetailText(text = "소모 칼로리 = ${caloriePerEachSet * workoutList.sumOf { (_, count) -> count }} kcal")
    }
}

@Composable
private fun ScoreSection(
    modifier: Modifier = Modifier,
    type: ScoreType,
    score: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DetailText(
            text = type.value,
            color = when(type) {
                ScoreType.PERFECT -> ColorSaturday
                ScoreType.GOOD -> ColorLightGreen
                ScoreType.NOT_GOOD -> ColorSunday
            },
            fontSize = 18.dpToSp(),
            fontWeight = FontWeight.Black
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .width(5.dp),
            thickness = 2.dp,
            color = Color.LightGray
        )
        DetailText(
            text = "${score}회",
            color = Color.White.copy(
                alpha = when(type) {
                    ScoreType.PERFECT -> 1.0f
                    ScoreType.GOOD -> 0.9f
                    ScoreType.NOT_GOOD -> 0.8f
                }
            ),
            fontSize = 18.dpToSp()
        )
    }
}

@Composable
private fun FoodInfo(
    modifier: Modifier = Modifier,
    foodName: String,
    calorie: Float,
    foodCount: Int
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = ColorPrimary,
                shape = AbsoluteRoundedCornerShape(5.dp)
            )
            .border(
                width = 2.dp,
                color = Color.LightGray,
                shape = AbsoluteRoundedCornerShape(5.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = foodName,
                    color = Color.White,
                    fontSize = 16.dpToSp(),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fontFamily
                )
                Text(
                    text = "× $foodCount",
                    color = Color.White,
                    fontSize = 16.dpToSp(),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fontFamily
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$calorie kcal",
                    color = Color.White,
                    fontSize = 16.dpToSp(),
                    fontWeight = FontWeight.Medium,
                    fontFamily = fontFamily
                )
                Text(
                    text = "( = ${calorie * foodCount.toFloat()}) kcal",
                    color = Color.White,
                    fontSize = 16.dpToSp(),
                    fontWeight = FontWeight.Medium,
                    fontFamily = fontFamily
                )
            }
        }
    }
}

@Composable
private fun Separator(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = ColorSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}