package com.overeasy.smartfitness.scenario.diary.diarydetail

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.model.diary.DiaryDetail
import com.overeasy.smartfitness.getDateString
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val dietRepository: DietRepository
) : ViewModel() {
    private val _diaryDetailUiEvent = MutableSharedFlow<DiaryDetailUiEvent>()
    val diaryDetailUiEvent = _diaryDetailUiEvent.asSharedFlow()

    // 수정
    private val _diaryDetail = MutableStateFlow<DiaryDetail?>(null)
    val diaryDetail = _diaryDetail.asStateFlow()

    private val _workoutVideoDataList = mutableStateListOf<List<Pair<String, String>>>()
    val workoutVideoDataList = _workoutVideoDataList

    private val _dietHistoryList = mutableStateListOf<Triple<String, Float, Int>>()
    val dietHistoryList = _dietHistoryList

    fun onLoad(noteId: Int, noteDate: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            requestGetDiaryDetail(noteId)
            requestGetWorkoutVideoList(noteId)
            requestGetDietsHistory(noteDate ?: getDateString())
        }
    }

    private suspend fun requestGetDiaryDetail(noteId: Int) {
        ApiRequestHelper.makeRequest {
            workoutRepository.getWorkoutNoteDetail(noteId)
        }.onSuccess { res ->
            _diaryDetail.value = res.result.run {
                DiaryDetail(
                    totalKcal = totalKcal,
                    totalPerfect = totalPerfect,
                    totalGood = totalGood,
                    totalBad = totalBad,
                    totalScore = totalScore,
                    workoutList = workoutList
                )
            }
        }.onFailure { res ->
//            _diaryDetail.value = DiaryDetail(
//                totalKcal = 150,
//                totalPerfect = 11,
//                totalGood = 6,
//                totalBad = 3,
//                totalScore = 2400,
//                workoutList = listOf(
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 0,
//                        noteId = 0,
//                        exerciseName = "벤치프레스",
//                        setNum = 1,
//                        repeats = 10,
//                        weight = 60,
//                    ),
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 0,
//                        noteId = 0,
//                        exerciseName = "벤치프레스",
//                        setNum = 2,
//                        repeats = 15,
//                        weight = 70,
//                    ),
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 0,
//                        noteId = 0,
//                        exerciseName = "벤치프레스",
//                        setNum = 3,
//                        repeats = 15,
//                        weight = 80,
//                    ),
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 0,
//                        noteId = 0,
//                        exerciseName = "벤치프레스",
//                        setNum = 4,
//                        repeats = 10,
//                        weight = 50,
//                    ),
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 1,
//                        noteId = 1,
//                        exerciseName = "데드리프트",
//                        setNum = 1,
//                        repeats = 5,
//                        weight = 60,
//                    ),
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 1,
//                        noteId = 1,
//                        exerciseName = "데드리프트",
//                        setNum = 2,
//                        repeats = 10,
//                        weight = 70,
//                    ),
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 1,
//                        noteId = 1,
//                        exerciseName = "데드리프트",
//                        setNum = 3,
//                        repeats = 15,
//                        weight = 80,
//                    ),
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 1,
//                        noteId = 1,
//                        exerciseName = "데드리프트",
//                        setNum = 4,
//                        repeats = 20,
//                        weight = 90,
//                    ),
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 2,
//                        noteId = 2,
//                        exerciseName = "스쿼트",
//                        setNum = 1,
//                        repeats = 40,
//                        weight = 100,
//                    ),
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 2,
//                        noteId = 2,
//                        exerciseName = "스쿼트",
//                        setNum = 2,
//                        repeats = 30,
//                        weight = 90,
//                    ),
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 2,
//                        noteId = 2,
//                        exerciseName = "스쿼트",
//                        setNum = 3,
//                        repeats = 20,
//                        weight = 80,
//                    ),
//                    DiaryDetailWorkoutInfo(
//                        workoutId = 2,
//                        noteId = 2,
//                        exerciseName = "스쿼트",
//                        setNum = 4,
//                        repeats = 10,
//                        weight = 70,
//                    )
//                    /**
//                     * 벤치 3
//                     * 데드 4
//                     * 스쿼트 5
//                     */
//                    /**
//                     * 벤치 3
//                     * 데드 4
//                     * 스쿼트 5
//                     */
//                )
//            )
            println("jaehoLee", "onFailure: ${res.code}, ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError: ${throwable.message}")
        }
    }

    private suspend fun requestGetWorkoutVideoList(noteId: Int) {
        ApiRequestHelper.makeRequest {
            workoutRepository.getWorkoutVideoList(noteId)
        }.onSuccess { res ->
            _workoutVideoDataList.addAll(
                res.result.workoutVideoList.map { workoutVideoData ->
                    workoutVideoData.run { exerciseName to workoutVideoId }
                }.groupBy { (name, _) ->
                    name
                }.mapValues { (_, dataList) ->
                    dataList.sortedBy { (_, id) -> id }
                }.map { (_, dataList) ->
                    dataList.map { (workoutName, videoId) ->
                        workoutName to "${BuildConfig.BASE_URL}/workouts/video/stream/${videoId}"
                    }
                }
            )
        }.onFailure { res ->
            println("jaehoLee", "onFailure: ${res.code}, ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError: ${throwable.message}")
        }
    }

    private suspend fun requestGetDietsHistory(dietDate: String) {
        ApiRequestHelper.makeRequest {
            dietRepository.getDietsHistory(
                userId = MainApplication.appPreference.userId,
                dietDate = dietDate
            )
        }.onSuccess { res ->
            val groupedDietHistoryList = res.result.dietList.groupBy { dietHistory ->
                dietHistory.foodName
            }.map { (foodName, dietHistoryList) ->
                Triple(foodName, dietHistoryList.first().totalCalories, dietHistoryList.size)
            }

            _dietHistoryList.clear()
            _dietHistoryList.addAll(groupedDietHistoryList)
        }.onFailure { res ->
            println("jaehoLee", "onFailure: ${res.code}, ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError: ${throwable.message}")
        }
    }

    sealed class DiaryDetailUiEvent {

    }
}