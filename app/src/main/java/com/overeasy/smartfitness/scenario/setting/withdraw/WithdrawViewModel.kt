package com.overeasy.smartfitness.scenario.setting.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {
    private val _withdrawUiEvent = MutableSharedFlow<WithdrawUiEvent>()
    val withdrawUiEvent = _withdrawUiEvent.asSharedFlow()

    private val userId = MutableStateFlow<Int?>(null)

    init {
        viewModelScope.launch {
            userId.filterNotNull().collectLatest { id ->
                println("jaehoLee", "id = $id")
                ApiRequestHelper.makeRequest {
                    settingRepository.deleteUsers(id)
                }.onSuccess { res ->
                    println("jaehoLee", "onSuccess, res = $res")
                    MainApplication.appPreference.isLogin = false

                    _withdrawUiEvent.emit(WithdrawUiEvent.OnSuccessWithdraw)
                }.onFailure { res ->
                    println("jaehoLee", "onFailure, res = $res")
                    _withdrawUiEvent.emit(WithdrawUiEvent.OnFailureWithdraw)
                }.onError { throwable ->
                    println("jaehoLee", "onError, throwable = ${throwable.message}")
                }
            }
        }
    }

    fun onClickWithdraw() {
        userId.value = MainApplication.appPreference.userId
    }

    sealed class WithdrawUiEvent {
        data object OnSuccessWithdraw : WithdrawUiEvent()
        data object OnFailureWithdraw : WithdrawUiEvent()
    }
}