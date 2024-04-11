package com.overeasy.smartfitness.scenario.ranking.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.domain.ranking.model.RankingItem
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun RankingList(
    modifier: Modifier = Modifier,
    rankingItemList: List<RankingItem>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.scrollable(
            state = scrollState,
            orientation = Orientation.Vertical
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TOP 10",
            color = Color.Red,
            fontSize = 18.dpToSp(),
            fontWeight = FontWeight.ExtraBold,
            fontFamily = fontFamily
        )
        Spacer(modifier = Modifier.height(12.dp))
        rankingItemList.sortedByDescending { rankingItem ->
            rankingItem.score
        }.filterIndexed { index, _ ->
            index != rankingItemList.size - 1
        }.forEachIndexed { index, rankingItem ->
            RankingListItem(
                modifier = Modifier.padding(horizontal = 24.dp),
                rankingItem = rankingItem,
                rank = index + 1
            )

            if (index != rankingItemList.size - 2) {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Ellipsis()
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "로그인이 필요합니다.",
            color = ColorSecondary,
            fontSize = 24.dpToSp(),
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily
        )
//        RankingListItem(
//            modifier = Modifier.padding(horizontal = 24.dp),
//            rankingItem = rankingItemList.last(),
//            rank = rankingItemList.size
//        )
    }
}

@Composable
private fun Ellipsis(
    modifier: Modifier = Modifier,
    dotCount: Int = 3
) {
    Column(
        modifier = modifier
    ) {
        for (index in 1..dotCount) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = ColorSecondary,
                        shape = CircleShape
                    )
            )

            if (index != dotCount) {
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}