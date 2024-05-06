package com.overeasy.smartfitness.scenario.ranking.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.ui.theme.ColorLightBlack
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun RankingScreen(
    modifier: Modifier = Modifier,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()

    val categoryList = remember { viewModel.categoryList }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val rankingInfoList = remember { viewModel.rankingInfoList }
    val userRankingInfo by viewModel.userRankingInfo.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorLightBlack)
    ) {
        if (categoryList.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                CategoryList(
                    categoryList = categoryList,
                    selectedCategoryIndex = selectedCategoryIndex,
                    onClickCategoryItem = { category, index ->
                        selectedCategoryIndex = index
                        viewModel.onChangeCategory(category)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (rankingInfoList.isNotEmpty()) {
                    RankingList(
                        rankingInfoList = rankingInfoList,
                        userRankingInfo = userRankingInfo,
                        currentCategory = categoryList[selectedCategoryIndex]
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                    if (categoryList.isNotEmpty()) {
                        Text(
                            text = "${categoryList[selectedCategoryIndex]} 랭킹이\n존재하지 않아요.",
                            color = ColorSecondary,
                            fontSize = 20.dpToSp(),
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
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
                    text = "랭킹을 로딩 중이에요.\n잠시만 기다려주세요...",
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