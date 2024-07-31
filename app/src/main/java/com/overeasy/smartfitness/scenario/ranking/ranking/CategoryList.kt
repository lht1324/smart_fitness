@file:OptIn(ExperimentalLayoutApi::class)

package com.overeasy.smartfitness.scenario.ranking.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun CategoryList(
    modifier: Modifier = Modifier,
    categoryList: List<String>,
    selectedCategoryIndex: Int,
    onClickCategoryItem: (String, Int) -> Unit
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
    ) {
        categoryList.forEachIndexed { index, category ->
            CategoryListItem(
                modifier = Modifier.noRippleClickable {
                    onClickCategoryItem(category, index)
                },
                category = category,
                isSelected = index == selectedCategoryIndex
            )
        }
    }
}

@Composable
private fun CategoryListItem(
    modifier: Modifier = Modifier,
    category: String,
    isSelected: Boolean
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = AbsoluteRoundedCornerShape(10.dp)
            )
            .background(
                color = if (isSelected) {
                    Color.LightGray
                } else {
                    ColorPrimary
                },
                shape = AbsoluteRoundedCornerShape(10.dp)
            )
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 10.dp),
            text = category,
            color = if (isSelected) {
                ColorPrimary
            } else {
                Color.LightGray
            },
            fontSize = 12.dpToSp(),
            fontWeight = FontWeight.Light,
            fontFamily = fontFamily
        )
    }
}