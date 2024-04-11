package com.overeasy.smartfitness.scenario.diet.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.scenario.diet.diet.DietViewModel
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun DietResultScreen(
    modifier: Modifier = Modifier,
    viewModel: DietViewModel = hiltViewModel()
) {
    val recommendedDietList = listOf(
        ""
    )

    Column(
        modifier = modifier
    ) {

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