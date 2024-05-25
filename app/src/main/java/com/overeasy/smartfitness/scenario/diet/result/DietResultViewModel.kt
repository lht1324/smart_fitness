package com.overeasy.smartfitness.scenario.diet.result

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.diet.entity.PostDietsReq
import com.overeasy.smartfitness.domain.diet.model.UserMenu
import com.overeasy.smartfitness.getDateString
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.scenario.diet.diet.DietViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class DietResultViewModel @Inject constructor(
    private val dietRepository: DietRepository
) : ViewModel() {
    private val _dietResultUiEvent = MutableSharedFlow<DietResultUiEvent>()
    val dietResultUiEvent = _dietResultUiEvent.asSharedFlow()

    private val _foodRecommendList = mutableStateListOf<Triple<String, Float, String>>() // name, calorie, type
    val foodRecommendList = _foodRecommendList

    init {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                requestGetDietsRecommend()
            }
        }
    }

    fun onClickFinish(foodName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = MainApplication.appPreference.userId

            requestPostDiets(
                userId = userId,
                req = PostDietsReq(
                    dietList = listOf(
                        UserMenu(
                            userId = userId,
                            dietDate = getDateString(),
                            foodName = foodName
                        )
                    )
                )
            )
        }
    }

    private suspend fun event(uiEvent: DietResultUiEvent) {
        _dietResultUiEvent.emit(uiEvent)
    }

    private suspend fun requestGetDietsRecommend() {
        ApiRequestHelper.makeRequest {
            dietRepository.getDietsRecommend(MainApplication.appPreference.userId)
        }.onSuccess { res ->
            _foodRecommendList.clear()
            _foodRecommendList.addAll(
                res.result.foodRecommend.map { recommendedFood ->
                    recommendedFood.run { Triple(name, calorie, mainFoodType) }
                }
            )
        }.onFailure { res ->
            println("jaehoLee", "onFailure: $res")
        }.onError { throwable ->
            println("jaehoLee", "onError: ${throwable.message}")
        }
    }

    private suspend fun requestPostDiets(userId: Int, req: PostDietsReq) {
        ApiRequestHelper.makeRequest {
            dietRepository.postDiets(
                userId = userId,
                req = req
            )
        }.onSuccess { res ->
            event(DietResultUiEvent.OnSuccess)
        }.onFailure { res ->
            event(DietResultUiEvent.OnFailure)
            println("jaehoLee", "onFailure in requestPostDiets(): ${res.message}")
        }.onError { throwable ->
            event(DietResultUiEvent.OnFailure)
            println("jaehoLee", "onError in requestPostDiets(): ${throwable.message}")
        }
    }

    sealed class DietResultUiEvent {
        data object OnSuccess : DietResultUiEvent()
        data object OnFailure : DietResultUiEvent()
    }
}