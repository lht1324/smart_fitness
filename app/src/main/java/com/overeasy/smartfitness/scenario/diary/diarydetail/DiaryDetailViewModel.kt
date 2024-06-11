package com.overeasy.smartfitness.scenario.diary.diarydetail

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.BuildConfig
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.ai.AiRepository
import com.overeasy.smartfitness.domain.ai.dto.PostAiFeedbackReq
import com.overeasy.smartfitness.domain.base.getSuccessOrNull
import com.overeasy.smartfitness.domain.base.makeRequest
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.exercises.ExercisesRepository
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.dto.res.note.toEntity
import com.overeasy.smartfitness.domain.workout.dto.res.workout.toEntity
import com.overeasy.smartfitness.domain.workout.entity.DiaryDetail
import com.overeasy.smartfitness.domain.workout.entity.DiaryDetailWorkoutInfo
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
    private val exercisesRepository: ExercisesRepository,
    private val dietRepository: DietRepository,
    private val aiRepository: AiRepository
) : ViewModel() {
    private val _diaryDetailUiEvent = MutableSharedFlow<DiaryDetailUiEvent>()
    val diaryDetailUiEvent = _diaryDetailUiEvent.asSharedFlow()

    private val _diaryDetail = MutableStateFlow<DiaryDetail?>(null)
    val diaryDetail = _diaryDetail.asStateFlow()

    private val _workoutInfoList = MutableStateFlow<List<Pair<String, List<DiaryDetailWorkoutInfo>>>>(listOf())
    val workoutInfoList = _workoutInfoList.asStateFlow()

    private val _workoutVideoDataList = MutableStateFlow<List<Pair<String, List<String>>>>(listOf())
    val workoutVideoDataList = _workoutVideoDataList

    private val _dietHistoryList = mutableStateListOf<Triple<String, Float, Int>>()
    val dietHistoryList = _dietHistoryList

    private val _aiFeedbackList = mutableStateListOf<String>()
    val aiFeedbackList = _aiFeedbackList

    fun onLoad(
        noteId: Int,
        noteIdListString: String,
        noteDate: String?,
        workoutName: String,
        workoutResultIndexListString: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            initializeDiaryDetail(
                noteId = noteId,
                noteIdListString = noteIdListString,
                workoutName = workoutName,
                workoutResultIndexListString = workoutResultIndexListString
            )
            requestGetDietsHistory(noteDate ?: getDateString())
        }
    }

    private suspend fun initializeDiaryDetail(
        noteId: Int,
        noteIdListString: String,
        workoutName: String,
        workoutResultIndexListString: String
    ) {
        val exerciseDataList = getExerciseData()?.result?.exerciseList?.map { exerciseData ->
            exerciseData.run { exerciseName to perKcal }
        }

        if (noteIdListString.isNotEmpty()) {
            val splitNoteIdList = noteIdListString
                .split(",")
                .mapNotNull { it.toIntOrNull() }
                .sortedBy { noteId -> noteId }

            val diaryDetailList = splitNoteIdList.mapNotNull {  noteId ->
                requestGetWorkoutNoteDetail(noteId)?.result?.toEntity()
            }
            val videoDataList = splitNoteIdList.mapNotNull {  noteId ->
                requestGetWorkoutVideoList(noteId)?.result?.workoutVideoList?.map { data ->
                    data.toEntity()
                }
            }.flatten()

            _diaryDetail.value = DiaryDetail(
                perfectCount = diaryDetailList.sumOf { diaryDetail ->
                    diaryDetail.perfectCount
                },
                goodCount = diaryDetailList.sumOf { diaryDetail ->
                    diaryDetail.goodCount
                },
                notGoodCount = diaryDetailList.sumOf { diaryDetail ->
                    diaryDetail.notGoodCount
                },
                totalScore = diaryDetailList.sumOf { diaryDetail ->
                    diaryDetail.totalScore
                },
                totalKcal = diaryDetailList.map { diaryDetail ->
                    diaryDetail.totalKcal
                }.filter { totalKcal ->
                    totalKcal != 0
                }.sum(),
                workoutInfoList = listOf(),
            )
            _workoutInfoList.value = diaryDetailList.filter { diaryDetail ->
                diaryDetail.workoutInfoList.isNotEmpty()
            }.sortedBy { diaryDetail ->
                diaryDetail.workoutInfoList.first().noteId
            }.map { diaryDetail ->
                diaryDetail.workoutInfoList.first().workoutName to
                        diaryDetail.workoutInfoList.distinctBy { workoutInfo ->
                            workoutInfo.setCount
                        }.sortedBy { workoutInfo ->
                            workoutInfo.setCount
                        }.map { workoutInfo ->
                            val caloriePerEachSet = exerciseDataList?.find { (name, _) ->
                                name == workoutInfo.workoutName
                            }?.second ?: 0

                            if (caloriePerEachSet != 0) {
                                workoutInfo.copy(
                                    caloriePerEachCount = caloriePerEachSet
                                )
                            } else {
                                workoutInfo
                            }
                        }
            }
            _workoutVideoDataList.value = videoDataList.groupBy { videoData ->
                videoData.exerciseName
            }.map { (workoutName, list) ->
                workoutName to list.sortedBy { videoData ->
                    videoData.noteId
                }.map { videoData ->
                    "${BuildConfig.BASE_URL}/workouts/video/stream/${videoData.workoutVideoId}"
                }
            }
        } else {
            if (noteId != -1) {
                val getWorkoutNoteDetailRes = requestGetWorkoutNoteDetail(noteId)?.result?.toEntity()
                _diaryDetail.value = getWorkoutNoteDetailRes

                if (getWorkoutNoteDetailRes?.workoutInfoList?.isNotEmpty() == true) {
                    _workoutInfoList.value = listOf(
                        workoutName to getWorkoutNoteDetailRes.workoutInfoList.map { workoutInfo ->
                            val caloriePerEachSet = exerciseDataList?.find { (name, _) ->
                                name == workoutInfo.workoutName
                            }?.second ?: 0

                            if (caloriePerEachSet != 0) {
                                workoutInfo.copy(
                                    caloriePerEachCount = caloriePerEachSet
                                )
                            } else {
                                workoutInfo
                            }
                        }
                    )
                }

                val videoDataList = requestGetWorkoutVideoList(noteId)?.result?.workoutVideoList
                    ?.map { data ->
                        data.toEntity()
                    } ?: listOf()

                _workoutVideoDataList.value = videoDataList.groupBy { videoData ->
                    videoData.exerciseName
                }.map { (workoutName, list) ->
                    workoutName to list.sortedBy { videoData ->
                        videoData.noteId
                    }.map { videoData ->
                        "${BuildConfig.BASE_URL}/workouts/video/stream/${videoData.workoutVideoId}"
                    }
                }

                requestPostAiFeedback(
                    workoutName = workoutName,
                    workoutResultIndexList = workoutResultIndexListString
                        .split("/")
                        .map { setListString ->
                            setListString
                                .split(",")
                                .mapNotNull { resultIndexString ->
                                    resultIndexString.toIntOrNull()
                                }.filter { resultIndex ->
                                    resultIndex in 0..31
                                }
                        }
                )
            }
        }
    }

    private suspend fun requestGetWorkoutNoteDetail(noteId: Int) = run {
        makeRequest {
            workoutRepository.getWorkoutNoteDetail(noteId)
        }.onFailure { res ->
            println("jaehoLee", "onFailure: ${res.code}, ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError: ${throwable.message}")
        }.getSuccessOrNull()
    }

    private suspend fun requestGetWorkoutVideoList(noteId: Int) = run {
        makeRequest {
            workoutRepository.getWorkoutVideoList(noteId)
        }.onFailure { res ->
            println("jaehoLee", "onFailure in requestGetWorkoutVideoList(): ${res.code}, ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError in requestGetWorkoutVideoList(): ${throwable.message}")
        }.getSuccessOrNull()
    }

    private suspend fun requestGetDietsHistory(dietDate: String) {
        makeRequest {
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
            println("jaehoLee", "onFailure in requestGetDietsHistory(): ${res.code}, ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError in requestGetDietsHistory(): ${throwable.message}")
        }
    }

    private suspend fun requestPostAiFeedback(
        workoutName: String,
        workoutResultIndexList: List<List<Int>>
    ) {
        makeRequest {
            aiRepository.postAiFeedback(
                PostAiFeedbackReq(
                    workoutName = workoutName,
                    workoutResultIndexList = workoutResultIndexList
                )
            )
        }.onSuccess { res ->
            _aiFeedbackList.clear()
            _aiFeedbackList.addAll(res.result.feedback)
        }.onFailure { res ->
            println("jaehoLee", "onFailure in requestPostAiFeedback(): ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError in requestPostAiFeedback(): ${throwable.message}")
        }
    }

    private suspend fun getExerciseData() = run {
        makeRequest {
            exercisesRepository.getExercises()
        }.onFailure { res ->
            println("jaehoLee", "onFailure in getExerciseKcalPerACount(): ${res.code}, ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError in getExerciseKcalPerACount(): ${throwable.message}")
        }.getSuccessOrNull()
    }

    sealed class DiaryDetailUiEvent {

    }
}