package com.overeasy.smartfitness.scenario.ranking.ranking

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.addCommaIntoNumber
import com.overeasy.smartfitness.domain.ranking.model.RankingInfo
import com.overeasy.smartfitness.domain.ranking.model.RankingItem
import com.overeasy.smartfitness.model.ranking.UserTier
import com.overeasy.smartfitness.ui.theme.ColorBronze
import com.overeasy.smartfitness.ui.theme.ColorGold
import com.overeasy.smartfitness.ui.theme.ColorIron
import com.overeasy.smartfitness.ui.theme.ColorMaster
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.ColorSilver

@Composable
fun RankingListItem(
    modifier: Modifier = Modifier,
    nickname: String,
    score: Int,
    rank: Int
) {
    Box(
        modifier = modifier.border(
            width = 1.dp,
            color = ColorSecondary.getAlphaColorByRank(rank),
            shape = AbsoluteRoundedCornerShape(5.dp)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "${rank}위",
                modifier = Modifier.weight(0.20f),
                color = ColorSecondary.getAlphaColorByRank(rank),
                textAlign = TextAlign.Start
            )
            Text(
                text = nickname,
                modifier = Modifier.weight(0.40f),
                color = Color.White.getAlphaColorByRank(rank),
                textAlign = TextAlign.Start
            )
            Text(
                text = "${addCommaIntoNumber(score)}점",
                modifier = Modifier.weight(0.40f),
                color = Color.White.getAlphaColorByRank(rank),
                textAlign = TextAlign.End
            )
        }
    }
}

private fun Color.getAlphaColorByRank(rank: Int) = copy(
    alpha = if (rank in 1..10) {
        (-(rank - 11).toFloat() / 100.0f) * 4f + 0.60f
    } else {
        1f
    }
)