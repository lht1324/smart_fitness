package com.overeasy.smartfitness.scenario.setting.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginReq
import com.overeasy.smartfitness.isLettersOrDigits
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
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
class LoginViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {
    private val _loginUiEvent = MutableSharedFlow<LoginUiEvent>()
    val loginUiEvent = _loginUiEvent.asSharedFlow()

    private val _id = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    val id = _id.asStateFlow()
    val password = _password.asStateFlow()

    val isIdInvalid = _id.map { id ->
        !(id.length < 10 && isLettersOrDigits(id)) && id.isNotEmpty()
    }
    val isPasswordInvalid = _password.map { password ->
        password.length !in 6..15 && password.isNotEmpty()
    }

    private val isClickedLoginButton = MutableStateFlow(false)

    fun onChangeId(value: String) {
        _id.value = value
    }

    fun onChangePassword(value: String) {
        _password.value = value
    }

    fun onClickLogin() {
        isClickedLoginButton.value = true
    }

    init {
        viewModelScope.launch {
            launch {
                combine(id, password) { id, password ->
                    id to password
                }.flatMapLatest { (id, password) ->
                    isClickedLoginButton.filter { isClicked ->
                        isClicked
                    }.map {
                        id to password
                    }
                }.collectLatest { (id, password) ->
                    ApiRequestHelper.makeRequest {
                        settingRepository.postUsersLogin(
                            PostUsersLoginReq(
                                username = id,
                                password = password
                            )
                        )
                    }.onSuccess { res ->
                        println("jaehoLee", "onSuccess: $res")
                        isClickedLoginButton.value = false

                        MainApplication.appPreference.isLogin = true
                        MainApplication.appPreference.userId = res.result?.id ?: -1 // ?.toIntOrNull() ?: -1

                        _loginUiEvent.emit(LoginUiEvent.OnFinishLogin)
                    }.onFailure { res ->
                        println("jaehoLee", "onFailure: $res")
                        isClickedLoginButton.value = false

                        _loginUiEvent.emit(LoginUiEvent.ShowFailedDialog)
                    }.onError { throwable ->
                        println("jaehoLee", "onError: ${throwable.message}")
                        isClickedLoginButton.value = false
                    }
                }
            }
        }
    }

    sealed class LoginUiEvent {
        data object OnFinishLogin : LoginUiEvent()
        data object ShowFailedDialog : LoginUiEvent()
    }
}