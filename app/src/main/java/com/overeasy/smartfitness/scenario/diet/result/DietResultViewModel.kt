package com.overeasy.smartfitness.scenario.diet.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.diet.model.DietRecommendResult
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.scenario.diet.diet.FoodCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DietResultViewModel @Inject constructor(
    private val dietRepository: DietRepository
) : ViewModel() {
    private val _dietResultUiEvent = MutableSharedFlow<DietResultUiEvent>()
    val dietResultUiEvent = _dietResultUiEvent.asSharedFlow()

    private val selectedCategory = MutableStateFlow<FoodCategory?>(null)

    private val _foodRecommendList = MutableStateFlow<List<List<String>>>(listOf())
    val foodRecommendList = _foodRecommendList.asStateFlow()

    init {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                selectedCategory.filterNotNull().collectLatest { category ->
                    ApiRequestHelper.makeRequest {
                        dietRepository.getDiet(category.name.lowercase())
                    }.onSuccess { res ->
                        delay(5000L) // test
                        println("jaehoLee", "onSuccess: $res")
                        _foodRecommendList.value = listOf(
                            res.result.foodRecommend,
                            res.result.foodRecommend,
                            res.result.foodRecommend,
                            res.result.foodRecommend,
                            res.result.foodRecommend,
                        )
                    }.onFailure { res ->
                        println("jaehoLee", "onFailure: $res")
                    }.onError { throwable ->
                        println("jaehoLee", "onError: ${throwable.message}")
                    }
                }
            }
        }
    }

    fun setCategory(category: FoodCategory) {
        selectedCategory.value = category
    }

    sealed class DietResultUiEvent {

    }
}