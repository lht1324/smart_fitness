package com.overeasy.smartfitness.scenario.diet.diet

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.overeasy.smartfitness.R
import com.overeasy.smartfitness.domain.diet.model.CategoryItem
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.scenario.diet.diet.DietViewModel
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun DietScreen(
    modifier: Modifier = Modifier,
    viewModel: DietViewModel = hiltViewModel(),
    onClickCategoryItem: (FoodCategory) -> Unit
) {
    var isShowFinishDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()


    val categoryList = listOf(
        CategoryItem(FoodCategory.KOREAN),
        CategoryItem(FoodCategory.CHINESE),
        CategoryItem(FoodCategory.JAPANESE),
        CategoryItem(FoodCategory.WESTERN),
        CategoryItem(FoodCategory.FASTFOOD),
        CategoryItem(FoodCategory.DESSERT)
    ) // 이후 viewModel로 수정

    var isImageLoadingFinished by remember { mutableStateOf(false) }

    val screenState by viewModel.screenState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        when (screenState) {
            DietScreenState.NEED_LOGIN, DietScreenState.NEED_INPUT_BODY_INFO -> {
                Text(
                    text = if (screenState == DietScreenState.NEED_LOGIN) {
                        "로그인이 필요합니다."
                    } else {
                        "'설정 → 내 정보'에서\n키와 몸무게를 입력해주세요."
                    },
                    modifier = Modifier.align(Alignment.Center),
                    color = ColorSecondary,
                    fontSize = 24.dpToSp(),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center
                )
            }

            DietScreenState.NORMAL -> {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .verticalScroll(state = scrollState)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        for (index in 0..<((categoryList.size + 1) / 2)) {
                            // 2n, 2n + 1
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val firstItem = categoryList[2 * index]
                                val secondItem = categoryList[2 * index + 1]

                                FoodCategoryItem(
                                    modifier = Modifier,
                                    drawableId = firstItem.drawableId,
                                    category = firstItem.foodCategory.value,
                                    onTransformState = { state ->
                                        isImageLoadingFinished =
                                            state is AsyncImagePainter.State.Success

                                        state
                                    },
                                    onClickItem = {
                                        onClickCategoryItem(firstItem.foodCategory)
                                    }
                                )
                                FoodCategoryItem(
                                    drawableId = secondItem.drawableId,
                                    category = secondItem.foodCategory.value,
                                    onTransformState = { state ->
                                        isImageLoadingFinished =
                                            state is AsyncImagePainter.State.Success

                                        state
                                    },
                                    onClickItem = {
                                        onClickCategoryItem(secondItem.foodCategory)
                                    }
                                )
                            }
                            if (index < (categoryList.size + 1) / 2 - 1) {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
                if (!isImageLoadingFinished) {
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
            else -> {

            }
        }
    }
}

@Composable
private fun FoodCategoryItem(
    modifier: Modifier = Modifier,
    @DrawableRes drawableId: Int,
    category: String,
    onTransformState: ((AsyncImagePainter.State) -> AsyncImagePainter.State)? = null,
    onClickItem: () -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Column(
        modifier = modifier
            .width((screenWidth.toFloat() / 2f).dp)
            .noRippleClickable {
                onClickItem()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = drawableId,
            contentDescription = null,
            modifier = Modifier
                .size((screenWidth.toFloat() / 2f / 3f * 2f).dp)
                .clip(CircleShape),
            transform = if (onTransformState != null) {
                onTransformState
            } else {
                { state ->
                    state
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = category,
            color = Color.White,
            fontSize = 18.dpToSp(),
            fontWeight = FontWeight.ExtraBold,
            fontFamily = fontFamily
        )
    }
}

@Composable
private fun Separator(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(15.dp))
    }
}

private fun translateKoreanToEnglish() {

}