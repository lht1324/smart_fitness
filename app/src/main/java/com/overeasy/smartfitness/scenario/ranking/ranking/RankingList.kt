package com.overeasy.smartfitness.scenario.ranking.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.score.dto.RankingInfo
import com.overeasy.smartfitness.domain.score.dto.RankingUserInfo
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.ui.theme.Color919191
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun RankingList(
    modifier: Modifier = Modifier,
    rankingInfoList: List<RankingInfo>,
    userRankingInfo: RankingUserInfo?,
    currentCategory: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TOP 10",
            color = ColorSecondary,
            fontSize = 18.dpToSp(),
            fontWeight = FontWeight.ExtraBold,
            fontFamily = fontFamily
        )
        Spacer(modifier = Modifier.height(12.dp))
        rankingInfoList.sortedByDescending { rankingInfo ->
            rankingInfo.score
        }.forEachIndexed { index, rankingInfo ->
            RankingListItem(
                nickname = rankingInfo.nickname,
                score = rankingInfo.score,
                rank = index + 1
            )

            if (index != rankingInfoList.size - 1) {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Ellipsis()
        Spacer(modifier = Modifier.height(10.dp))

        if (MainApplication.appPreference.isLogin) {
            if (userRankingInfo != null) {
                RankingListItem(
                    nickname = userRankingInfo.nickname,
                    score = userRankingInfo.score,
                    rank = userRankingInfo.ranking
                )
            } else {
                Text(
                    text = "$currentCategory 운동 기록이\n존재하지 않아요.",
                    color = ColorSecondary,
                    fontSize = 20.dpToSp(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                text = "로그인이 필요해요.",
                color = ColorSecondary,
                fontSize = 20.dpToSp(),
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )
        }
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
                    .size(7.dp)
                    .background(
                        color = Color919191,
                        shape = CircleShape
                    )
            )

            if (index != dotCount) {
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}