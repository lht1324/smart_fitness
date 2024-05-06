package com.overeasy.smartfitness.scenario.diary.diarydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.domain.diary.DiaryRepository
import com.overeasy.smartfitness.domain.diary.model.DiaryDetail
import com.overeasy.smartfitness.domain.diary.model.DiaryDetailWorkoutInfo
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
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    private val _diaryDetailUiEvent = MutableSharedFlow<DiaryDetailUiEvent>()
    val diaryDetailUiEvent = _diaryDetailUiEvent.asSharedFlow()

    // 수정
    private val _diaryDetail = MutableStateFlow<DiaryDetail?>(null)
    val diaryDetail = _diaryDetail.asStateFlow()

    fun onLoad(noteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            requestGetDiaryDetail(noteId)
        }
    }

    private suspend fun requestGetDiaryDetail(noteId: Int) {
        ApiRequestHelper.makeRequest {
            diaryRepository.getDiaryDetail(noteId)
        }.onSuccess { res ->
            _diaryDetail.value = res.run {
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
            println("jaehoLee", "onFailure: ${res.code}, ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError: ${throwable.message}")
        }
    }

    sealed class DiaryDetailUiEvent {

    }
}