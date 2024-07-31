package com.overeasy.smartfitness.scenario.setting.findid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.domain.base.makeRequest
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.isLettersOrDigitsIncludeKorean
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindIdViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {
    private val _findIdUiEvent = MutableSharedFlow<FindIdUiEvent>()
    val findIdUiEvent = _findIdUiEvent.asSharedFlow()

    private val _nickname = MutableStateFlow("")
    private val _age = MutableStateFlow("")

    val nickname = _nickname.asStateFlow()
    val age = _age.asStateFlow()

    val isNicknameInvalid = _nickname.map { nickname ->
        !(nickname.length <= 8 && isLettersOrDigitsIncludeKorean(nickname)) && nickname.isNotEmpty()
    }

    val isAgeInvalid = _age.map { age ->
        val intAge = (age.toIntOrNull() ?: -1)

        intAge !in 0..< 100 && age.isNotEmpty()
    }

    private val isClickedFindButton = MutableStateFlow(false)

    fun onChangeNickname(value: String) {
        _nickname.value = value
    }

    fun onChangeAge(value: String) {
        if (value.toIntOrNull() != null || value.isEmpty()) {
            _age.value = if (value.toIntOrNull() != null && value.startsWith("0")) {
                value.toInt().toString()
            } else {
                value
            }
        }
    }

    fun onClickFindButton() {
        isClickedFindButton.value = true
    }

    init {
        viewModelScope.launch {
            launch {
                combine(nickname, age) { nickname, age ->
                    nickname to age
                }.flatMapLatest { (nickname, age) ->
                    isClickedFindButton.filter { isClicked ->
                        isClicked
                    }.map {
                        nickname to age
                    }
                }.collectLatest { (nickname, age) ->
                    makeRequest {
                        settingRepository.getUsers(
                            nickname = nickname,
                            age = age.toInt()
                        )
                    }.onSuccess { res ->
                        println("jaehoLee", "onSuccess: $res")
                        isClickedFindButton.value = false

                        _findIdUiEvent.emit(FindIdUiEvent.OnFinishFindId(res.result))
                    }.onFailure { res ->
                        println("jaehoLee", "onFailure: $res")
                        isClickedFindButton.value = false

                        _findIdUiEvent.emit(FindIdUiEvent.ShowFailedDialog)
                    }.onError { throwable ->
                        println("jaehoLee", "onError: ${throwable.message}")
                        isClickedFindButton.value = false

                        _findIdUiEvent.emit(FindIdUiEvent.ShowFailedDialog)
                    }
                }
            }
        }
    }

    sealed class FindIdUiEvent {
        data class OnFinishFindId(val id: String) : FindIdUiEvent()
        data object ShowFailedDialog : FindIdUiEvent()
    }
}