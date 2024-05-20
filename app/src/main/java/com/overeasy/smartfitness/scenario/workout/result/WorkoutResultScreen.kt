package com.overeasy.smartfitness.scenario.workout.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.R
import com.overeasy.smartfitness.addCommaIntoNumber
import com.overeasy.smartfitness.domain.workout.model.workout.SetCount
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.pxToDp
import com.overeasy.smartfitness.ui.theme.ColorLightGreen
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.ColorSunday
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun WorkoutResultScreen(
    modifier: Modifier = Modifier,
    viewModel: WorkoutResultViewModel = hiltViewModel(),
    noteId: Int
) {
    val scrollState = rememberScrollState()
    val workoutResult by viewModel.workoutResult.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
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
                    workoutResult.workoutList.forEachIndexed { index, workout ->
                        WorkoutSection(
                            name = workout.name,
                            workoutList = workout.setCountList,
                            caloriePerEachSet = workout.calorieUsage
                        )
                        if (index != workoutResult.workoutList.size - 1) {
                            SectionDivider()
                        }
                    }

                    SectionDivider()

                    DetailText(
                        text = "점수", // sumOf
                        fontSize = 18.dpToSp()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    workoutResult.workoutScoreList.forEachIndexed { index, (name, score) ->
                        Score(
                            name = name,
                            score = score,
                            index = index
                        )
                        if (index != workoutResult.workoutScoreList.size - 1) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    SectionDivider()

                    if (workoutResult.menuList.isNullOrEmpty()) {
                        DetailText(
                            text = "식단 추천을 받으면 보너스 점수를 받을 수 있어요.", // sumOf
                            color = Color.LightGray,
                            fontSize = 14.dpToSp(),
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    DetailText(
                        text = "총 소모 칼로리",
                        fontSize = 20.dpToSp(),
                        fontWeight = FontWeight.ExtraBold
                    )

                    DetailText(
                        text = if (workoutResult.menuList.isNullOrEmpty()) {
                            val totalCalorieUsage = workoutResult.workoutList.sumOf { workout ->
                                workout.setCountList.sumOf { (set, count) ->
                                    set * count
                                } * workout.calorieUsage
                            }
                            "${addCommaIntoNumber(totalCalorieUsage)} kcal"
                        } else {
                            val totalCalorieUsage = workoutResult.workoutList.sumOf { workout ->
                                workout.setCountList.sumOf { (set, count) ->
                                    set * count
                                } * workout.calorieUsage
                            }
                            val basalMetabolicRate = 1800

                            "${addCommaIntoNumber(totalCalorieUsage)} + ${addCommaIntoNumber(basalMetabolicRate)} (기초대사량) kcal"
                        },
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailText(
                        text = "총점",
                        fontSize = 20.dpToSp(),
                        fontWeight = FontWeight.ExtraBold
                    )
                    DetailText(
                        text = if (workoutResult.menuList.isNullOrEmpty()) {
                            "${addCommaIntoNumber(workoutResult.workoutTotalScore)} kcal"
                        } else {
                            "${addCommaIntoNumber(workoutResult.workoutTotalScore)} + 600 = 3,000 kcal"
                        },
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.ExtraBold
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                color = ColorSecondary,
                                shape = AbsoluteRoundedCornerShape(10.dp)
                            )
                            .align(Alignment.End)
                    ) {
                        Text(
                            text = "운동 영상\n저장하기",
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
                            color = Color.White,
                            fontSize = 18.dpToSp(),
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DetailText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.White,
    fontSize: TextUnit = 16.dpToSp(),
    fontWeight: FontWeight = FontWeight.Bold
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontFamily = fontFamily
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
                color = ColorSecondary,
                fontSize = 20.dpToSp(),
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
    workoutList: List<SetCount>
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
            text = "$name (1회 당 $caloriePerEachSet kcal 소모)",
            fontSize = 18.dpToSp()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
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
                workoutList.forEach { (set, count) ->
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
        DetailText(text = "소모 칼로리 = ${workoutList.sumOf { (_, count) -> caloriePerEachSet * count }} kcal")
    }
}

@Composable
private fun Score(
    modifier: Modifier = Modifier,
    name: String,
    score: Int,
    index: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DetailText(
            text = name,
            color = when (index) {
                0 -> ColorSaturday
                1 -> ColorLightGreen
                else -> ColorSunday
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
            text = "$score",
            fontSize = 18.dpToSp()
        )
    }
}

@Composable
private fun SectionDivider(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = ColorSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}