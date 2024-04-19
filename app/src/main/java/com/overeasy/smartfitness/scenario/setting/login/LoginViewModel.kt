package com.overeasy.smartfitness.scenario.setting.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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
        id.isEmpty()
    }
    val isPasswordInvalid = _password.map { password ->
        password.isEmpty()
    }

    private val onClickLoginEvent = MutableSharedFlow<Unit>()

    fun onChangeId(value: String) {
        _id.value = value
    }

    fun onChangePassword(value: String) {
        _password.value = value
    }

    fun onClickLogin() {
        viewModelScope.launch {
            onClickLoginEvent.emit(Unit)
        }
    }

    init {
        viewModelScope.launch {
            launch {
                onClickLoginEvent.flatMapLatest {
                    combine(id, password) { id, password ->
                        id to password
                    }
                }.map { (id, password) ->
                    settingRepository.postUsersLogin(
                        id = id,
                        password = password
                    )
                }.collectLatest { res ->
                    if (res.status == 200) {
                        MainApplication.appPreference.isLogin = true

                        _loginUiEvent.emit(LoginUiEvent.OnFinishLogin("성공"))
                    } else {
                        _loginUiEvent.emit(LoginUiEvent.OnFinishLogin("실패, ${res.message}"))
                    }
                }
            }
        }
    }

    sealed class LoginUiEvent {
        //        data object OnFinishLogin : LoginUiEvent()
        data class OnFinishLogin(val msg: String) : LoginUiEvent()
    }
}