package com.overeasy.smartfitness.scenario.diet.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.overeasy.smartfitness.domain.diet.model.CategoryItem
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.scenario.diet.diet.FoodCategory
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun DietResultScreen(
    modifier: Modifier = Modifier,
    viewModel: DietResultViewModel = hiltViewModel(),
    foodCategory: FoodCategory,
    onFinish: () -> Unit
) {
    val scrollState = rememberScrollState()

    var isShowDialog by remember { mutableStateOf(false) }
    var selectedDietNumber by remember { mutableIntStateOf(0) }

    val foodRecommendList by viewModel.foodRecommendList.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize()
                .verticalScroll(state = scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "추천 결과 (${foodCategory.value})",
                    color = Color.White,
                    fontSize = 24.dpToSp(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
                Spacer(modifier = Modifier.weight(1f))
                AsyncImage(
                    model = CategoryItem(foodCategory).drawableId,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "원하는 식단을 선택해주세요.",
                color = Color.Gray,
                fontSize = 18.dpToSp(),
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(10.dp))
            foodRecommendList.forEachIndexed { index, foodRecommend ->
                RecommendedDiet(
                    modifier = Modifier
                        .fillMaxWidth()
                        .noRippleClickable {
                            selectedDietNumber = index + 1
                            isShowDialog = true
                        },
                    number = index + 1,
                    menuList = foodRecommend
                )
                if (index != foodRecommendList.size - 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
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

    if (isShowDialog) {
        Dialog(
            title = "${selectedDietNumber}번 식단을 선택하시겠어요?",
            confirmText = "취소",
            dismissText = "선택하기",
            onClickConfirm = {
                isShowDialog = false
            },
            onClickDismiss = {
                onFinish()
                isShowDialog = false
            },
            onDismissRequest = {
                isShowDialog = false
            }
        )
    }

    LaunchedEffect(foodCategory) {
        viewModel.setCategory(foodCategory)
    }
}

@Composable
private fun RecommendedDiet(
    modifier: Modifier = Modifier,
    number: Int,
    menuList: List<String>
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "추천 ${number}.",
            color = Color.White,
            fontSize = 20.dpToSp(),
            fontWeight = FontWeight.ExtraBold,
            fontFamily = fontFamily
        )

        Spacer(modifier = Modifier.height(10.dp))

        menuList.forEachIndexed { index, menu ->
            Text(
                text = menu,
                color = Color.White,
                fontSize = 16.dpToSp(),
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )
            if (index != menuList.size - 1) {
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}