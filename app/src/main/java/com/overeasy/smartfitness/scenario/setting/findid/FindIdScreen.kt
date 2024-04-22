package com.overeasy.smartfitness.scenario.setting.findid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.setting.public.InfoInputField
import com.overeasy.smartfitness.scenario.setting.public.SettingButton
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FindIdScreen(
    modifier: Modifier = Modifier,
    viewModel: FindIdViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    var isShowSuccessDialog by remember { mutableStateOf(false) }
    var isShowFailedDialog by remember { mutableStateOf(false) }

    val nickname by viewModel.nickname.collectAsState()
    val age by viewModel.age.collectAsState()

    val isNicknameInvalid by viewModel.isNicknameInvalid.collectAsState(initial = false)
    val isAgeInvalid by viewModel.isAgeInvalid.collectAsState(initial = false)

    var id by remember { mutableStateOf("")}

    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        InfoInputField(
            value = nickname,
            onValueChange = viewModel::onChangeNickname,
            placeholder = "닉네임",
            isInvalid = isNicknameInvalid,
            invalidText = "닉네임은 8자 이하의 영문, 숫자, 한글로 입력되어야 해요."
        )
        InfoInputField(
            value = age,
            onValueChange = viewModel::onChangeAge,
            placeholder = "나이",
            isInvalid = isAgeInvalid,
            invalidText = "100세 미만의 나이만 입력할 수 있어요."
        )
        Spacer(modifier = Modifier.height(5.dp))
        SettingButton(
            modifier = Modifier.align(Alignment.End),
            text = "아이디 찾기",
            onClick = {
                if (!isNicknameInvalid && !isAgeInvalid && nickname.isNotEmpty() && age.isNotEmpty()) {
                    viewModel.onClickFindButton()
                }
            }
        )
    }

    if (isShowSuccessDialog) {
        Dialog(
            title = "아이디를 찾았어요!",
            description = "회원님의 아이디는\n'${id}'에요.",
            confirmText = "취소",
            dismissText = "돌아가기",
            onClickConfirm = {
                isShowSuccessDialog = false
            },
            onClickDismiss = {
                onFinish()
                isShowSuccessDialog = false
            },
            onDismissRequest = {
                isShowSuccessDialog = false
            }
        )
    }

    if (isShowFailedDialog) {
        Dialog(
            title = "아이디를 찾지 못 했어요.",
            description = "정보를 다시 한 번 확인해 주실 수 있나요?",
            confirmText = "확인하기",
            onClickConfirm = {
                isShowFailedDialog = false
            },
            onDismissRequest = {
                isShowFailedDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.findIdUiEvent.collectLatest { event ->
            when (event) {
                is FindIdViewModel.FindIdUiEvent.OnFinishFindId -> {
                    id = event.id
                    isShowSuccessDialog = true
                }
                FindIdViewModel.FindIdUiEvent.ShowFailedDialog -> {
                    isShowFailedDialog = true
                }
            }
        }
    }
}