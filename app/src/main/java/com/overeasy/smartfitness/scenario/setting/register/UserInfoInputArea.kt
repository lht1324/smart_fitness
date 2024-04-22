package com.overeasy.smartfitness.scenario.setting.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.setting.public.InfoInputField
import com.overeasy.smartfitness.scenario.setting.public.SettingButton

@Composable
fun UserInfoInputArea(
    modifier: Modifier = Modifier,
    id: String,
    password: String,
    isIdInvalid: Boolean,
    isPasswordInvalid: Boolean,
    finishButtonText: String = "입력 완료",
    onChangeId: (String) -> Unit,
    onChangePassword: (String) -> Unit,
    onClickFinish: () -> Unit
) {
    var isShowDialog by remember { mutableStateOf(false) }

    var currentId by remember { mutableStateOf(id) }
    var currentPassword by remember { mutableStateOf(password) }
    var currentIsIdInvalid by remember { mutableStateOf(isIdInvalid) }
    var currentIsPasswordInvalid by remember { mutableStateOf(isPasswordInvalid) }

    val dialogTitle by remember {
        derivedStateOf {
            when {
                currentId.isEmpty() && currentPassword.isEmpty() -> {
                    "아이디와 비밀번호가 입력되지 않았어요."
                }
                currentId.isEmpty() && currentPassword.isNotEmpty() -> {
                    "아이디가 입력되지 않았어요."
                }
                currentId.isNotEmpty() && currentPassword.isEmpty() -> {
                    "비밀번호가 입력되지 않았어요."
                }
                currentIsIdInvalid && currentIsPasswordInvalid -> {
                    "아이디와 비밀번호를 잘못 입력했어요."
                }
                currentIsIdInvalid && !currentIsPasswordInvalid -> {
                    "아이디를 잘못 입력했어요."
                }
                !currentIsIdInvalid && currentIsPasswordInvalid -> {
                    "비밀번호를 잘못 입력했어요."
                }
                else -> ""
            }
        }
    }
    val dialogDescription by remember {
        derivedStateOf {
            when {
                currentId.isEmpty() && currentPassword.isEmpty() -> {
                    "아이디와 비밀번호를 입력해주세요."
                }
                currentId.isEmpty() && currentPassword.isNotEmpty() -> {
                    "아이디를 입력해주세요."
                }
                currentId.isNotEmpty() && currentPassword.isEmpty() -> {
                    "비밀번호를 입력해주세요."
                }
                currentIsIdInvalid && currentIsPasswordInvalid -> {
                    "다시 확인해주세요."
                }
                currentIsIdInvalid && !currentIsPasswordInvalid -> {
                    "아이디는 10자 미만이고 영문 혹은 숫자로 만들어져야 해요."
                }
                !currentIsIdInvalid && currentIsPasswordInvalid -> {
                    "비밀번호는 6자 이상 16자 미만이어야 해요."
                }
                else -> ""
            }
        }
    }

    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        InfoInputField(
            value = id,
            onValueChange = onChangeId,
            placeholder = "아이디",
            invalidText = "아이디는 10자 미만이고 영문 혹은 숫자로 만들어져야 해요.",
            isInvalid = isIdInvalid
        )
        InfoInputField(
            value = password,
            onValueChange = onChangePassword,
            placeholder = "비밀번호",
            isInvalid = isPasswordInvalid,
            invalidText = "비밀번호는 6자 이상 16자 미만이어야 해요.",
            isMaskedTextField = true
        )
        Spacer(modifier = Modifier.height(5.dp))
        SettingButton(
            modifier = Modifier.align(Alignment.End),
            text = finishButtonText,
            onClick = {
                when {
                    !isIdInvalid && !isPasswordInvalid && id.isNotEmpty() && password.isNotEmpty() -> onClickFinish()
                    else -> {
                        isShowDialog = true
                    }
                }
            }
        )
    }

    if (isShowDialog) {
        Dialog(
            title = dialogTitle,
            description = dialogDescription,
            confirmText = "다시 하기",
            onClickConfirm = {
                isShowDialog = false
            },
            onDismissRequest = {
                isShowDialog = false
            }
        )
    }

    LaunchedEffect(id, password, isIdInvalid, isPasswordInvalid) {
        currentId = id
        currentPassword = password
        currentIsIdInvalid = isIdInvalid
        currentIsPasswordInvalid = isPasswordInvalid
    }
}