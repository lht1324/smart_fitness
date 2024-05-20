package com.overeasy.smartfitness.scenario.setting.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    registerState: RegisterState,
    onChangeRegisterState: (RegisterState) -> Unit,
    onFinishRegister: () -> Unit,
) {
    var isShowErrorDialog by remember { mutableStateOf(false) }
    var isShowAlreadyExistDialog by remember { mutableStateOf(false) }

    val id by viewModel.id.collectAsState()
    val password by viewModel.password.collectAsState()

    val nickname by viewModel.nickname.collectAsState()

    val bodyInfo by viewModel.bodyInfo.collectAsState()

    val isIdInvalid by viewModel.isIdInvalid.collectAsState(initial = false)
    val isPasswordInvalid by viewModel.isPasswordInvalid.collectAsState(initial = false)

    val isNicknameInvalid by viewModel.isNicknameInvalid.collectAsState(initial = false)

    val isAgeInvalid by viewModel.isAgeInvalid.collectAsState(initial = false)
    val isHeightInvalid by viewModel.isHeightInvalid.collectAsState(initial = false)
    val isWeightInvalid by viewModel.isWeightInvalid.collectAsState(initial = false)

    var currentRegisterState by remember { mutableStateOf(RegisterState.UserInfoInput) }
    val currentRegisterArea by remember {
        derivedStateOf<@Composable () -> Unit> {
            @Composable {
                when (currentRegisterState) {
                    RegisterState.UserInfoInput -> UserInfoInputArea(
                        id = id,
                        password = password,
                        isIdInvalid = isIdInvalid,
                        isPasswordInvalid = isPasswordInvalid,
                        onChangeId = viewModel::onChangeId,
                        onChangePassword = viewModel::onChangePassword,
                        onClickFinish = {
                            currentRegisterState = RegisterState.NicknameInput
                        }
                    )

                    RegisterState.NicknameInput -> NicknameInputArea(
                        nickname = nickname,
                        isNicknameInvalid = isNicknameInvalid,
                        onChangeNickname = viewModel::onChangeNickname,
                        onFinish = {
                            currentRegisterState = RegisterState.BodyInfoInput
                        }
                    )

                    RegisterState.BodyInfoInput -> BodyInfoInputArea(
                        age = bodyInfo.age ?: "",
                        height = bodyInfo.height ?: "",
                        weight = bodyInfo.weight ?: "",
                        onChangeAge = viewModel::onChangeAge,
                        onChangeHeight = viewModel::onChangeHeight,
                        onChangeWeight = viewModel::onChangeWeight,
                        onChangeGender = viewModel::onChangeGender,
                        isAgeInvalid = isAgeInvalid,
                        isHeightInvalid = isHeightInvalid,
                        isWeightInvalid = isWeightInvalid,
                        onClickFinish = {
                            currentRegisterState = RegisterState.TasteInfoInput
                        }
                    )

                    RegisterState.TasteInfoInput -> TasteInfoInputArea(
                        onChangeSpicyPreference = viewModel::onChangeSpicyPreference,
                        onChangeMeatConsumption = viewModel::onChangeMeatConsumption,
                        onChangeTastePreference = viewModel::onChangeTastePreference,
                        onChangeActivityLevel = viewModel::onChangeActivityLevel,
                        onChangePreferenceTypeFood = viewModel::onChangePreferenceTypeFood,
                        onClickSkipTasteInput = viewModel::onClickSkipTasteInfo,
                        onFinishTasteInfoInput = viewModel::onClickRegister
                    )
                }
            }
        }
    }

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        currentRegisterArea()
    }

    if (isShowErrorDialog) {
        Dialog(
            title = "이런! 회원가입에 실패했어요... \uD83D\uDE25",
            description = "다시 시도 해주실 수 있을까요?",
            confirmText = "다시 시도",
            onClickConfirm = {
                isShowErrorDialog = false
            },
            onDismissRequest = {
                isShowErrorDialog = false
            }
        )
    }

    if (isShowAlreadyExistDialog) {
        Dialog(
            title = "이미 존재하는 정보에요.",
            description = "이미 존재하는 아이디네요.\n다시 작성해주실 수 있을까요?",
            confirmText = "다시 작성하기",
            onClickConfirm = {
                isShowAlreadyExistDialog = false
            },
            onDismissRequest = {
                isShowAlreadyExistDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.registerUiEvent.collectLatest { event ->
            when (event) {
                RegisterViewModel.RegisterUiEvent.OnFinishRegister -> {
                    onFinishRegister()
                }
                RegisterViewModel.RegisterUiEvent.UserInfoAlreadyExist -> {
                    isShowAlreadyExistDialog = true
                }
                RegisterViewModel.RegisterUiEvent.ShowFailedDialog -> {
                    isShowErrorDialog = true
                }
            }
        }
    }

    LaunchedEffect(currentRegisterState) {
        onChangeRegisterState(currentRegisterState)
    }

    LaunchedEffect(registerState) {
        if (registerState != currentRegisterState) {
            currentRegisterState = registerState
        }
    }
}