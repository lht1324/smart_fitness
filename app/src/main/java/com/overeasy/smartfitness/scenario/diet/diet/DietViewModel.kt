package com.overeasy.smartfitness.scenario.diet.diet

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.base.makeRequest
import com.overeasy.smartfitness.domain.setting.SettingRepository
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
class DietViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {
    private val _dietUiEvent = MutableSharedFlow<DietUiEvent>()
    val dietUiEvent = _dietUiEvent.asSharedFlow()

    private val _screenState = MutableStateFlow(DietScreenState.IDLE)
    val screenState = _screenState.asStateFlow()

    private val _userMenuList = mutableStateListOf("")
    val userMenuList = _userMenuList

    init {
        viewModelScope.launch {
            launch {
                if (MainApplication.appPreference.isLogin) {
                    makeRequest {
                        settingRepository.getUsersById(MainApplication.appPreference.userId)
                    }.onSuccess { res ->
                        _screenState.value = DietScreenState.NORMAL
                    }.onFailure { res ->
                        println("jaehoLee", "onFailure of getUsersById(): (${res.code}), ${res.message}")
                    }.onError { throwable ->
                        println("jaehoLee", "onError of getUsersById(): ${throwable.message}")
                    }
                } else {
                    _screenState.value = DietScreenState.NEED_LOGIN
                }
            }
        }
    }

    fun onClickAddUserMenu() {
        _userMenuList.add("")
    }

    fun onClickDeleteUserMenu(index: Int) {
        val originalUserMenuList = userMenuList.toList()

        _userMenuList.clear()
        _userMenuList.addAll(
            originalUserMenuList.take(index) + originalUserMenuList.takeLast(originalUserMenuList.size - (index + 1))
        )
    }

    fun onClickFinishInput() {
        viewModelScope.launch(Dispatchers.IO) {
            event(
                DietUiEvent.OnFinishInputMenu(
                    userMenuList.toList().filter { userMenu ->
                        userMenu.isNotEmpty()
                    }.map { userMenu ->
                        userMenu.replace(" ", "")
                    }.joinToString(",") { userMenu ->
                        userMenu
                    }
                )
            )
        }
    }

    fun onChangeUserMenu(value: String, index: Int) {
        if (Regex("^[ㄱ-ㅎㅏ-ㅣ가-힣 ]*\$").matches(value)) {
            _userMenuList[index] = value
        }
    }

    fun onFinishRecommendedDietSelect() {
        _userMenuList.clear()
        _userMenuList.add("")
    }

    private suspend fun event(uiEvent: DietUiEvent) {
        _dietUiEvent.emit(uiEvent)
    }

    sealed class DietUiEvent {
        data class OnFinishInputMenu(val userMenu: String) : DietUiEvent()
        data object OnSuccessInputMenu : DietUiEvent()
        data object OnFailureInputMenu : DietUiEvent()
    }
}