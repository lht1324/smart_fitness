package com.overeasy.smartfitness.scenario.diary.diarydetail

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.R
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.pxToDp
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.ColorSunday
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun DiaryDetailScreen(
    modifier: Modifier = Modifier,
    date: String
) {
    val scrollState = rememberScrollState()

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
                    WorkoutSection(
                        name = "벤치 프레스",
                        workoutList = listOf(
                            1 to 10,
                            2 to 15,
                            3 to 10,
                            4 to 15,
                        ),
                        caloriePerEachSet = 5
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailText(text = "소모 칼로리 = 150 kcal")
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = ColorSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    WorkoutSection(
                        name = "데드 리프트",
                        workoutList = listOf(
                            1 to 5,
                            2 to 10,
                            3 to 10,
                            4 to 5,
                        ),
                        caloriePerEachSet = 4
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailText(text = "소모 칼로리 = 150 kcal")
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = ColorSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    WorkoutSection(
                        name = "스쿼트",
                        workoutList = listOf(
                            1 to 10,
                            2 to 10,
                            3 to 20,
                            4 to 20,
                        ),
                        caloriePerEachSet = 3
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailText(text = "소모 칼로리 = 150 kcal")
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = ColorSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailText(
                        text = "점수", // sumOf
                        fontSize = 18.dpToSp()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DetailText(
                            text = "Cool", // sumOf
                            color = ColorSaturday,
                            fontSize = 18.dpToSp(),
                            fontWeight = FontWeight.Black
                        )
                        Divider(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .width(5.dp)
                                .height(2.dp),
                            color = Color.LightGray
                        )
                        DetailText(
                            text = "11", // sumOf
                            fontSize = 18.dpToSp()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DetailText(
                            text = "Good",
                            color = Color(0xFF2DFE54),
                            fontSize = 18.dpToSp(),
                            fontWeight = FontWeight.Black
                        )
                        Divider(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .width(5.dp)
                                .height(2.dp),
                            color = Color.LightGray
                        )
                        DetailText(
                            text = "6",
                            fontSize = 18.dpToSp()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DetailText(
                            text = "Not Good",
                            color = ColorSunday,
                            fontSize = 18.dpToSp(),
                            fontWeight = FontWeight.Black
                        )
                        Divider(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .width(5.dp)
                                .height(2.dp),
                            color = Color.LightGray
                        )
                        DetailText(
                            text = "3",
                            fontSize = 18.dpToSp()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = ColorSecondary
                    )
//                    Spacer(modifier = Modifier.height(10.dp))
//                    DetailText(
//                        text = "식단 추천을 받으면 보너스 점수를 받을 수 있어요.", // sumOf
//                        color = Color.LightGray,
//                        fontSize = 14.dpToSp(),
//                        fontWeight = FontWeight.ExtraBold
//                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailText(
                        text = "총 소모 칼로리", // sumOf
                        fontSize = 20.dpToSp(),
                        fontWeight = FontWeight.Black
                    )
//                    DetailText(
//                        text = "550 kcal", // sumOf
//                        fontSize = 18.dpToSp(),
//                        fontWeight = FontWeight.Black
//                    )
                    DetailText(
                        text = "550 + 1,800 (기초대사량) = 2,350 kcal", // sumOf
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    DetailText(
                        text = "총점", // sumOf
                        fontSize = 20.dpToSp(),
                        fontWeight = FontWeight.Black
                    )
//                    DetailText(
//                        text = "2,400",
//                        fontSize = 18.dpToSp(),
//                        fontWeight = FontWeight.Black
//                    )
                    DetailText(
                        text = "2,400 + 600 = 3,000",
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.Black
                    )
                }
            )
            Spacer(modifier = Modifier.height(30.dp))
            ResultArea(
                title = "식단",
                contents = {
                    val list = listOf(
                        "탄수화물" to "10g",
                        "단백질" to "10g",
                        "지방" to "10g",
                        "당" to "10g",
                        "나트륨" to "10g",
                        "콜레스테롤" to "10g"
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "추천 결과 (한식)",
                            color = Color.White,
                            fontSize = 24.dpToSp(),
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = fontFamily
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Image(
                            painter = painterResource(id = R.drawable.food_category_korean),
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    DietSection(
                        name = "흑미밥",
                        ingredientList = list
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DietSection(
                        name = "미역국",
                        ingredientList = list
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DietSection(
                        name = "김치제육볶음",
                        ingredientList = list
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DietSection(
                        name = "미역줄기무침",
                        ingredientList = list
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DietSection(
                        name = "배추김치",
                        ingredientList = list
                    )
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
//        Spacer(modifier = Modifier.height(10.dp))
//        DetailText(text = "소모 칼로리 = $usageCalorie kcal")
    }
}

@Composable
private fun DietSection(
    modifier: Modifier = Modifier,
    name: String,
    ingredientList: List<Pair<String, String>>
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailText(
                modifier = Modifier.onSizeChanged { (_, height) ->
                    if (dividerHeight != height) {
                        dividerHeight = height
                    }
                },
                text = name
            )
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
                ingredientList.forEachIndexed { index, (name, value) ->
                    Row {
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
                            text = name,
                            fontSize = 14.dpToSp()
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        DetailText(
                            text = value,
                            fontSize = 14.dpToSp()
                        )
                    }
                }
            }
        }
    }
}