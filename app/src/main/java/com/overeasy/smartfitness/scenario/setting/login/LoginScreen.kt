package com.overeasy.smartfitness.scenario.setting.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.setting.register.UserInfoInputArea
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onFinishLogin: () -> Unit
) {
    var isShowDialog by remember { mutableStateOf(false) }

    val id by viewModel.id.collectAsState()
    val password by viewModel.password.collectAsState()

    val isIdInvalid by viewModel.isIdInvalid.collectAsState(initial = false)
    val isPasswordInvalid by viewModel.isPasswordInvalid.collectAsState(initial = false)

    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        UserInfoInputArea(
            id = id,
            password = password,
            isIdInvalid = isIdInvalid,
            isPasswordInvalid = isPasswordInvalid,
            finishButtonText = "로그인",
            onChangeId = viewModel::onChangeId,
            onChangePassword = viewModel::onChangePassword,
            onClickFinish = {
                when {
                    !isIdInvalid && !isPasswordInvalid && id.isNotEmpty() && password.isNotEmpty() -> {
                        viewModel.onClickLogin()
                    }
                    else -> {
                        isShowDialog = true
                    }
                }
            }
        )
    }

    if (isShowDialog) {
        Dialog(
            title = "로그인에 실패했어요.",
            description = "아이디, 비밀번호를 다시 확인해주세요.",
            confirmText = "다시 하기",
            onClickConfirm = {
                isShowDialog = false
            },
            onDismissRequest = {
                isShowDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loginUiEvent.collectLatest { event ->
            when (event) {
                is LoginViewModel.LoginUiEvent.OnFinishLogin -> {
                    onFinishLogin()
                }
                LoginViewModel.LoginUiEvent.ShowFailedDialog -> {
                    isShowDialog = true
                }
            }
        }
    }
}