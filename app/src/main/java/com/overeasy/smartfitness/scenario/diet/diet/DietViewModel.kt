package com.overeasy.smartfitness.scenario.diet.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.setting.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DietViewModel @Inject constructor(
    private val dietRepository: DietRepository,
    private val settingRepository: SettingRepository
) : ViewModel() {
    private val _screenState = MutableStateFlow(DietScreenState.IDLE)
    val screenState = _screenState.asStateFlow()

    init {
        viewModelScope.launch {
            if (MainApplication.appPreference.isLogin) {
                ApiRequestHelper.makeRequest {
                    settingRepository.getUsers(MainApplication.appPreference.userId)
                }.onSuccess { res ->
                    if (res.result?.run { age != null && height != null && weight != null } == true) {
                        _screenState.value = DietScreenState.NORMAL
                    } else {
                        _screenState.value = DietScreenState.NEED_INPUT_BODY_INFO
                    }
                }.onFailure { res ->

                }
            } else {
                _screenState.value = DietScreenState.NEED_LOGIN
            }
        }
    }
}