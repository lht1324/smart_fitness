package com.overeasy.smartfitness.scenario.setting.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.setting.public.SettingButton
import com.overeasy.smartfitness.scenario.setting.public.SettingTextField
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
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        SettingTextField(
            value = id,
            onValueChange = viewModel::onChangeId,
            placeholder = "아이디",
            isInvalid = isIdInvalid
        )
        Spacer(modifier = Modifier.height(10.dp))
        SettingTextField(
            value = password,
            onValueChange = viewModel::onChangePassword,
            placeholder = "비밀번호",
            isInvalid = isPasswordInvalid
        )
        Spacer(modifier = Modifier.height(10.dp))
        SettingButton(
            modifier = Modifier.align(Alignment.End),
            text = "로그인",
            onClick = viewModel::onClickLogin
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