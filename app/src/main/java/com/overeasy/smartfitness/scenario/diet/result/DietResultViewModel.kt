package com.overeasy.smartfitness.scenario.diet.result

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.base.makeRequest
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.diet.dto.toEntity
import com.overeasy.smartfitness.domain.diet.dto.PostDietsRecommendSelectReq
import com.overeasy.smartfitness.domain.diet.entity.RecommendedFood
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DietResultViewModel @Inject constructor(
    private val dietRepository: DietRepository
) : ViewModel() {
    private val _dietResultUiEvent = MutableSharedFlow<DietResultUiEvent>()
    val dietResultUiEvent = _dietResultUiEvent.asSharedFlow()

    private val _recommendedFoodList = mutableStateListOf<RecommendedFood>() // name, calorie, type
    val recommendedFoodList = _recommendedFoodList

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
        makeRequest {
            dietRepository.postDietsRecommend(req)
        }.onSuccess { res ->
            _recommendedFoodList.clear()
            _recommendedFoodList.addAll(
                res.result.foodRecommend.map { foodRecommend ->
                    foodRecommend.toEntity()
                }.shuffled(Random(System.currentTimeMillis())) // 같은 리스트 내려오는 이슈 땜빵용 로직
            )
            println("jaehoLee", "recommmend = ${res.result.foodRecommend.map { it.name }}")
        }.onFailure { res ->
            event(DietResultUiEvent.OnFailureRecommend)
            println("jaehoLee", "onFailure of requestPostDiets(): ${res.message}")
        }.onError { throwable ->
            event(DietResultUiEvent.OnFailureRecommend)
            println("jaehoLee", "onError of requestPostDiets(): ${throwable.message}")
        }
    }

    private suspend fun requestPostDietsRecommendSelect(req: PostDietsRecommendSelectReq) {
        makeRequest {
            dietRepository.postDietsRecommendSelect(
                req = req
            )
        }.onSuccess { res ->
            event(DietResultUiEvent.OnSuccess)
        }.onFailure { res ->
            event(DietResultUiEvent.OnFailureSelect)
            println("jaehoLee", "onFailure in requestPostDietsRecommendSelect(): ${res.message}")
        }.onError { throwable ->
            event(DietResultUiEvent.OnFailureSelect)
            println("jaehoLee", "onError in requestPostDietsRecommendSelect(): ${throwable.message}")
        }
    }

    sealed class DietResultUiEvent {
        data object OnSuccess : DietResultUiEvent()
        data object OnFailureRecommend : DietResultUiEvent()
        data object OnFailureSelect : DietResultUiEvent()
    }
}