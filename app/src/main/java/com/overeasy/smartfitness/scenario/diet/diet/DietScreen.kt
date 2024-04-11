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
import com.overeasy.smartfitness.R
import com.overeasy.smartfitness.domain.diet.model.CategoryItem
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.scenario.diet.diet.DietViewModel
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun DietScreen(
    modifier: Modifier = Modifier,
    viewModel: DietViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    var isShowFinishDialog by remember { mutableStateOf(false) }
    val categoryList = listOf(
        CategoryItem("한식"),
        CategoryItem("중식"),
        CategoryItem("일식"),
        CategoryItem("양식"),
        CategoryItem("패스트푸드"),
        CategoryItem("디저트")
    ) // 이후 viewModel로 수정

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
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
                            drawableId = firstItem.drawableId,
                            category = firstItem.name
                        )
                        FoodCategoryItem(
                            drawableId = secondItem.drawableId,
                            category = secondItem.name
                        )
                    }
                    if (index < (categoryList.size + 1) / 2 - 1) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
//        if (true) {
//            Box(
//                modifier = modifier
//                    .fillMaxSize()
//                    .background(color = ColorPrimary)
//            ) {
//                Column(
//                    modifier = Modifier.align(Alignment.Center),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(100.dp),
//                        color = ColorSaturday,
//                        strokeWidth = 15.dp
//                    )
//                    Spacer(modifier = Modifier.height(10.dp))
//                    Text(
//                        text = "AI가 사용자님에게 맞는 식단을\n열심히 만들고 있어요.\n잠시만 기다려주세요...",
//                        color = Color.White,
//                        fontSize = 24.dpToSp(),
//                        fontWeight = FontWeight.ExtraBold,
//                        fontFamily = fontFamily,
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//        }
    }
}

@Composable
private fun FoodCategoryItem(
    modifier: Modifier = Modifier,
    @DrawableRes drawableId: Int,
    category: String
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Column(
        modifier = modifier
            .width((screenWidth.toFloat() / 2f).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = drawableId),
            modifier = Modifier
                .size((screenWidth.toFloat() / 2f / 3f * 2f).dp)
                .clip(CircleShape),
            contentDescription = null
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