package com.overeasy.smartfitness.scenario.setting.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.setting.public.InfoInputField
import com.overeasy.smartfitness.scenario.setting.public.SettingButton
import com.overeasy.smartfitness.scenario.setting.public.SettingTextField

@Composable
fun NicknameInputArea(
    modifier: Modifier = Modifier,
    nickname: String,
    onChangeNickname: (String) -> Unit,
    isNicknameInvalid: Boolean,
    onFinish: () -> Unit,
) {
    var isShowDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        InfoInputField(
            value = nickname,
            onValueChange = onChangeNickname,
            placeholder = "닉네임",
            isInvalid = isNicknameInvalid,
            invalidText = "닉네임은 8자 이하의 영문, 숫자, 한글로 입력되어야 해요."
        )
        Spacer(modifier = Modifier.height(10.dp))
        SettingButton(
            modifier = Modifier.align(Alignment.End),
            text = "입력 완료",
            onClick = {
                if (!isNicknameInvalid) {
                    onFinish()
                } else {
                    isShowDialog = true
                }
            }
        )
    }

    if (isShowDialog) {
        Dialog(
            title = "닉네임 입력을 실패했어요.",
            description = "닉네임은 8자 이하의 영문, 숫자, 한글로 입력되어야 해요.",
            confirmText = "다시 작성하기",
            onClickConfirm = {
                isShowDialog = false
            },
            onDismissRequest = {
                isShowDialog = false
            }
        )
    }
}