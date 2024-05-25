package com.overeasy.smartfitness.scenario.diet.diet

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.diet.DietRepository
import com.overeasy.smartfitness.domain.diet.entity.PostDietsReq
import com.overeasy.smartfitness.domain.diet.model.UserMenu
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.getDateString
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class DietViewModel @Inject constructor(
    private val dietRepository: DietRepository,
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
                    ApiRequestHelper.makeRequest {
                        settingRepository.getUsersById(MainApplication.appPreference.userId)
                    }.onSuccess { res ->
                        _screenState.value = DietScreenState.NORMAL
                    }.onFailure { res ->

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
            val userId = MainApplication.appPreference.userId

            requestPostDiets(
                userId = userId,
                req = PostDietsReq(
                    dietList = userMenuList.toList().filter { userMenu ->
                        userMenu.isNotEmpty()
                    }.map { userMenu ->
                        userMenu.replace(" ", "")
                    }.map { userMenu ->
                        UserMenu(
                            userId = userId,
                            dietDate = getDateString(),
                            foodName = userMenu
                        )
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

    private suspend fun event(uiEvent: DietUiEvent) {
        _dietUiEvent.emit(uiEvent)
    }

    private suspend fun requestGetDiets() {
        ApiRequestHelper.makeRequest {
            dietRepository.getDietsRecommend(MainApplication.appPreference.userId)
        }.onSuccess { res ->
            /* no-op */
        }.onFailure { res ->
            println("jaehoLee", "onFailure in requestGetDiets(): ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError in requestGetDiets(): ${throwable.message}")
        }
    }

    private suspend fun requestPostDiets(userId: Int, req: PostDietsReq) {
        ApiRequestHelper.makeRequest {
            dietRepository.postDiets(
                userId = userId,
                req = req
            )
        }.onSuccess { res ->
            _userMenuList.clear()
            _userMenuList.add("")
            event(DietUiEvent.OnSuccessInputMenu)
        }.onFailure { res ->
            println("jaehoLee", "onFailure in requestPostDiets(): ${res.message}")
            event(DietUiEvent.OnFailureInputMenu)
        }.onError { throwable ->
            println("jaehoLee", "onError in requestPostDiets(): ${throwable.message}")
        }
    }

    sealed class DietUiEvent {
        data object OnSuccessInputMenu : DietUiEvent()
        data object OnFailureInputMenu : DietUiEvent()
    }
}