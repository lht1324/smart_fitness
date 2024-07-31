package com.overeasy.smartfitness.scenario.setting.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.appConfig.MainApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(

) : ViewModel() {
    private val _logoutUiEvent = MutableSharedFlow<LogoutUiEvent>()
    val logoutUiEvent = _logoutUiEvent.asSharedFlow()

    fun onClickLogout() {
        viewModelScope.launch {
            MainApplication.appPreference.isLogin = false

            _logoutUiEvent.emit(LogoutUiEvent.OnSuccessLogout)
        }
    }

    sealed class LogoutUiEvent {
        data object OnSuccessLogout : LogoutUiEvent()
        data object OnFailureLogout : LogoutUiEvent()
    }
}