package com.overeasy.smartfitness.scenario.diet.result

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.diet.dto.toEntity
import com.overeasy.smartfitness.domain.diet.entity.PostDietsRecommendSelectReq
import com.overeasy.smartfitness.domain.diet.model.RecommendedFood
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DietResultViewModel @Inject constructor(
    private val dietRepository: DietRepository
) : ViewModel() {
    private val _dietResultUiEvent = MutableSharedFlow<DietResultUiEvent>()
    val dietResultUiEvent = _dietResultUiEvent.asSharedFlow()

    private val _recommendedFoodList = mutableStateListOf<RecommendedFood>() // name, calorie, type
    val recommendedFoodList = _recommendedFoodList

    init {
//        viewModelScope.launch {
//            launch(Dispatchers.IO) {
//                requestGetDietsRecommend()
//            }
//        }
    }

    fun onLoad(userMenu: String) {
        viewModelScope.launch {
            val userId = MainApplication.appPreference.userId

            requestPostDietsRecommend(
                req = PostDietsRecommendSelectReq(
                    userId = userId,
                    consumedFoodNames = userMenu.split(",")
                )
            )
        }
    }

    fun onClickFinish(foodName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = MainApplication.appPreference.userId

            requestPostDietsRecommendSelect(
                req = PostDietsRecommendSelectReq(
                    userId = userId,
                    consumedFoodNames = listOf(foodName)
                )
            )
        }
    }

    private suspend fun event(uiEvent: DietResultUiEvent) {
        _dietResultUiEvent.emit(uiEvent)
    }

    private suspend fun requestPostDietsRecommend(req: PostDietsRecommendSelectReq) {
        ApiRequestHelper.makeRequest {
            dietRepository.postDietsRecommend(req)
        }.onSuccess { res ->
            _recommendedFoodList.clear()
            _recommendedFoodList.addAll(
                res.result.foodRecommend.map { foodRecommend ->
                    foodRecommend.toEntity()
                }.sortedByDescending { recommendedFood ->
                    recommendedFood.similarityScore
                }
            )
        }.onFailure { res ->
            event(DietResultUiEvent.OnFailureRecommend)
            println("jaehoLee", "onFailure of requestPostDiets(): ${res.message}")
        }.onError { throwable ->
            event(DietResultUiEvent.OnFailureRecommend)
            println("jaehoLee", "onError of requestPostDiets(): ${throwable.message}")
        }
    }

    private suspend fun requestPostDietsRecommendSelect(req: PostDietsRecommendSelectReq) {
        ApiRequestHelper.makeRequest {
            dietRepository.postDietsRecommendSelect(
                req = req
            )
        }.onSuccess { res ->
            event(DietResultUiEvent.OnSuccess)
        }.onFailure { res ->
            event(DietResultUiEvent.OnFailureSelect)
            println("jaehoLee", "onFailure in requestPostDiets(): ${res.message}")
        }.onError { throwable ->
            event(DietResultUiEvent.OnFailureSelect)
            println("jaehoLee", "onError in requestPostDiets(): ${throwable.message}")
        }
    }

    sealed class DietResultUiEvent {
        data object OnSuccess : DietResultUiEvent()
        data object OnFailureRecommend : DietResultUiEvent()
        data object OnFailureSelect : DietResultUiEvent()
    }
}