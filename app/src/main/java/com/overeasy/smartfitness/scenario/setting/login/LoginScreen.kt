package com.overeasy.smartfitness.scenario.setting.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.scenario.setting.public.SettingButton
import com.overeasy.smartfitness.scenario.setting.public.SettingTextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onFinishLogin: (String) -> Unit
) {
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

    LaunchedEffect(Unit) {
        viewModel.loginUiEvent.collectLatest { event ->
            when (event) {
                is LoginViewModel.LoginUiEvent.OnFinishLogin -> {
                    onFinishLogin(event.msg)
                }
            }
        }
    }
}