package com.overeasy.smartfitness.scenario.ranking

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.overeasy.smartfitness.domain.ranking.model.RankingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(

) : ViewModel() {
    private val _rankingList = mutableStateListOf<RankingItem>(
        RankingItem(
            nickname = "김철수1",
            score = 1000,
            tier = "마스터"
        ),
        RankingItem(
            nickname = "김철수2",
            score = 900,
            tier = "마스터"
        ),
        RankingItem(
            nickname = "김철수3",
            score = 700,
            tier = "골드"
        ),
        RankingItem(
            nickname = "김철수34",
            score = 100,
            tier = "아이언"
        ),
        RankingItem(
            nickname = "김철수1345",
            score = 300,
            tier = "브론즈"
        ),
        RankingItem(
            nickname = "김철수3566",
            score = 200,
            tier = "아이언"
        ),
        RankingItem(
            nickname = "김철수15646",
            score = 400,
            tier = "브론즈"
        ),
        RankingItem(
            nickname = "김철수1646",
            score = 500,
            tier = "실버"
        ),
        RankingItem(
            nickname = "김철수18389",
            score = 800,
            tier = "골드"
        ),
        RankingItem(
            nickname = "김철수104949",
            score = 600,
            tier = "실버"
        ),
        RankingItem(
            nickname = "이재호",
            score = 500,
            tier = "아이언"
        )
    )
    val rankingList = _rankingList
}