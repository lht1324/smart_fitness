package com.overeasy.smartfitness.scenario.ranking.ranking

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.base.makeRequest
import com.overeasy.smartfitness.domain.exercises.ExercisesRepository
import com.overeasy.smartfitness.domain.score.ScoreRepository
import com.overeasy.smartfitness.domain.score.dto.RankingInfo
import com.overeasy.smartfitness.domain.score.dto.RankingUserInfo
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val exercisesRepository: ExercisesRepository
) : ViewModel() {
    private val currentCategory = MutableStateFlow("")

    private val _categoryList = mutableStateListOf<String>()
    val categoryList = _categoryList

    private val _rankingInfoList = mutableStateListOf<RankingInfo>()
    val rankingInfoList = _rankingInfoList

    private val _userRankingInfo = MutableStateFlow<RankingUserInfo?>(null)
    val userRankingInfo = _userRankingInfo.asStateFlow()

    init {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                requestGetExercises()
            }
            launch(Dispatchers.IO) {
                currentCategory.filter { category ->
                    category.isNotEmpty()
                }.distinctUntilChanged().collectLatest { category ->
                    requestGetRanking(category)

                    if (MainApplication.appPreference.isLogin) {
                        requestGetScoresUserByExerciseName(category)
                    } else {
                        _userRankingInfo.value = null
                    }
                }
            }
        }
    }

    fun onChangeCategory(category: String) {
        currentCategory.value = category
    }

    private suspend fun requestGetExercises() {
        makeRequest {
            exercisesRepository.getExercises()
//            rankingRepository.getRankingCategory()
        }.onSuccess { res ->
            _categoryList.clear()

            if (res.result?.exerciseList?.isNotEmpty() == true) {
                _categoryList.addAll(res.result.exerciseList.map { it.exerciseName })
                currentCategory.value = res.result.exerciseList.first().exerciseName
            }

//            if (res.result.scoreCategory.isNotEmpty()) {
//                _categoryList.addAll(res.result.scoreCategory)
//                currentCategory.value = res.result.scoreCategory[0]
//            } else {
//                _categoryList.addAll(listOf("푸쉬업", "데드리프트", "딥스", "벤치프레스", "숄더프레스"))
//                currentCategory.value = "푸쉬업"
//            }
        }.onFailure { res ->
            println("jaehoLee", "onFailure: ${res.code}, ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError: ${throwable.message}")
        }
    }

    private suspend fun requestGetRanking(category: String) {
//        val temporaryRankingList = listOf(
//            RankingInfo(
//                nickname = "김철수1",
//                score = 1000
//            ),
//            RankingInfo(
//                nickname = "김철수2",
//                score = 900
//            ),
//            RankingInfo(
//                nickname = "김철수3",
//                score = 700
//            ),
//            RankingInfo(
//                nickname = "김철수34",
//                score = 100
//            ),
//            RankingInfo(
//                nickname = "김철수1345",
//                score = 300
//            ),
//            RankingInfo(
//                nickname = "김철수3566",
//                score = 200
//            ),
//            RankingInfo(
//                nickname = "김철수15646",
//                score = 400
//            ),
//            RankingInfo(
//                nickname = "김철수1646",
//                score = 500
//            ),
//            RankingInfo(
//                nickname = "김철수18389",
//                score = 800
//            ),
//            RankingInfo(
//                nickname = "김철수104949",
//                score = 600
//            )
//        )
        makeRequest {
            scoreRepository.getScoresByExerciseName(category)
        }.onSuccess { res ->
            _rankingInfoList.clear()

            if (res.result?.rankingInfoList?.isNotEmpty() == true) {
//                val list = listOf(
//                    RankingInfo(
//                        nickname = "김철수1345",
//                        score = 3300
//                    ),
//                    RankingInfo(
//                        nickname = "김철수3566",
//                        score = 2100
//                    ),
//                    RankingInfo(
//                        nickname = "김철수15646",
//                        score = 4300
//                    ),
//                    RankingInfo(
//                        nickname = "김철수1646",
//                        score = 1500
//                    ),
//                    RankingInfo(
//                        nickname = "김철수18389",
//                        score = 800
//                    ),
//                    RankingInfo(
//                        nickname = "김철수104949",
//                        score = 600
//                    ),
//                    RankingInfo(
//                        nickname = "김철수1345",
//                        score = 320
//                    ),
//                    RankingInfo(
//                        nickname = "김철수3566",
//                        score = 240
//                    ),
//                    RankingInfo(
//                        nickname = "김철수15646",
//                        score = 480
//                    ),
//                )

                _rankingInfoList.addAll(
                    res.result.rankingInfoList // + list
                )
            }
//            else
//                _rankingInfoList.addAll(temporaryRankingList)
        }.onFailure { res ->
            _rankingInfoList.clear()
            println("jaehoLee", "onFailure: ${res.code}, ${res.message}")
        }.onError { throwable ->
            _rankingInfoList.clear()
            println("jaehoLee", "onError: ${throwable.message}")
        }
    }

    private suspend fun requestGetScoresUserByExerciseName(category: String) {
        val userId = MainApplication.appPreference.userId

        if (userId != -1) {
            makeRequest {
                scoreRepository.getScoresUserByExerciseName(userId, category)
            }.onSuccess { res ->
                println("jaehoLee", "onSuccess: $res")
                _userRankingInfo.value = res.result
            }.onFailure { res ->
                _userRankingInfo.value = null
                println("jaehoLee", "onFailure: ${res.code}, ${res.message}")
            }.onError { throwable ->
                _userRankingInfo.value = null
                println("jaehoLee", "onError: ${throwable.message}")
            }
        } else {
            _userRankingInfo.value = null
        }
    }
}