@file:OptIn(ExperimentalLayoutApi::class)

package com.overeasy.smartfitness.scenario.diet.result

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.scenario.diet.public.DietTextButton
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.fontFamily
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DietResultScreen(
    modifier: Modifier = Modifier,
    viewModel: DietResultViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val scrollState = rememberScrollState()

    var isShowNeedAtLeastAnItemDialog by remember { mutableStateOf(false) }
    var isShowDoesWantToFinishDialog by remember { mutableStateOf(false) }
    var isShowFinishedDialog by remember { mutableStateOf(false) }
    var isShowFailedDialog by remember { mutableStateOf(false) }

    var selectedIndex by remember { mutableIntStateOf(-1) }
    val foodRecommendList = remember { viewModel.foodRecommendList }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .verticalScroll(state = scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "추천 결과",
                color = Color.White,
                fontSize = 24.dpToSp(),
                fontWeight = FontWeight.ExtraBold,
                fontFamily = fontFamily
            )
            Separator()
            Text(
                text = "원하는 메뉴를 하나만 선택해주세요.",
                color = Color.White,
                fontSize = 20.dpToSp(),
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(10.dp))
            DietTextButton(
                modifier = Modifier.align(Alignment.End),
                text = "완료",
                onClick = {
                    if (selectedIndex != -1) {
                        isShowDoesWantToFinishDialog = true
                    } else {
                        isShowNeedAtLeastAnItemDialog = true
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                foodRecommendList.forEachIndexed { index, (name, calorie, type) ->
                    RecommendedMenu(
                        modifier = Modifier
                            .noRippleClickable {
                                selectedIndex = if (selectedIndex == index) {
                                    -1
                                } else {
                                    index
                                }
                            },
                        menuName = name,
                        calorie = calorie,
                        menuType = type,
                        isSelected = selectedIndex == index
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        if (foodRecommendList.isEmpty()) {
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
                        text = "AI가 사용자님에게 맞는 식단을\n열심히 만들고 있어요.\n잠시만 기다려주세요...",
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

    if (isShowNeedAtLeastAnItemDialog) {
        Dialog(
            title = "아이템을 한 개 선택해주셔야 해요.",
            confirmText = "확인",
            onClickConfirm = {
                isShowNeedAtLeastAnItemDialog = false
            },
            onDismissRequest = {
                isShowNeedAtLeastAnItemDialog = false
            }
        )
    }

    if (isShowDoesWantToFinishDialog) {
        val (foodName, _, _) = foodRecommendList[selectedIndex]

        Dialog(
            title = "'${foodName}'을(를) 선택하시겠어요?",
            confirmText = "취소",
            dismissText = "선택하기",
            onClickConfirm = {
                isShowDoesWantToFinishDialog = false
            },
            onClickDismiss = {
                viewModel.onClickFinish(foodName)
                isShowDoesWantToFinishDialog = false
            },
            onDismissRequest = {
                isShowDoesWantToFinishDialog = false
            }
        )
    }

    if (isShowFinishedDialog) {
        val (foodName, _, _) = foodRecommendList[selectedIndex]

        Dialog(
            title = "'$foodName'이(가) 저장되었어요.",
            confirmText = "돌아가기",
            onClickConfirm = {
                onFinish()
                isShowFinishedDialog = false
            },
            onDismissRequest = {
                isShowFinishedDialog = false
            }
        )
    }

    if (isShowFailedDialog) {
        Dialog(
            title = "메뉴 저장이 실패했어요.",
            description = "다시 한 번 시도해주세요.",
            confirmText = "확인",
            onClickConfirm = {
                isShowFailedDialog = false
            },
            onDismissRequest = {
                isShowFailedDialog = false
            }
        )
    }

    LaunchedEffect(viewModel.dietResultUiEvent) {
        viewModel.dietResultUiEvent.collectLatest { event ->
            /* no-op */
            when (event) {
                DietResultViewModel.DietResultUiEvent.OnSuccess -> {
                    isShowFinishedDialog = true
                }
                DietResultViewModel.DietResultUiEvent.OnFailure -> {
                    isShowFailedDialog = true
                }
            }
        }
    }
}

@Composable
private fun RecommendedMenu(
    modifier: Modifier = Modifier,
    menuName: String,
    calorie: Float,
    menuType: String,
    isSelected: Boolean
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) {
                    Color.LightGray
                } else {
                    ColorPrimary
                },
                shape = AbsoluteRoundedCornerShape(5.dp)
            )
            .border(
                width = 2.dp,
                color = Color.LightGray,
                shape = AbsoluteRoundedCornerShape(5.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            Text(
                text = menuName,
                color = Color.White,
                fontSize = 18.dpToSp(),
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "$calorie kcal",
                color = Color.White,
                fontSize = 16.dpToSp(),
                fontWeight = FontWeight.Normal,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = menuType,
                color = Color.White,
                fontSize = 16.dpToSp(),
                fontWeight = FontWeight.Normal,
                fontFamily = fontFamily
            )
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
            thickness = 1.dp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}