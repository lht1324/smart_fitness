package com.overeasy.smartfitness.scenario.setting.finish

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun FinishScreen(
    modifier: Modifier = Modifier,
    finishState: String
) {
    val title by remember {
        derivedStateOf {
            when (finishState) {
                SettingFinishState.LoginFinish.value -> "로그인이 완료되었어요."
                SettingFinishState.RegisterFinish.value -> "회원가입이 완료되었어요."
                SettingFinishState.LogoutFinish.value -> "로그아웃이 완료되었어요."
                SettingFinishState.WithdrawFinish.value -> "탈퇴 처리가 완료되었습니다."
                else -> "죄송합니다. 에러가 발생했네요. \uD83D\uDE25"
            }
        }
    }

    val description by remember {
        derivedStateOf {
            when (finishState) {
                SettingFinishState.LoginFinish.value -> "그럼 운동을 시작해볼까요?"
                SettingFinishState.RegisterFinish.value -> "이제 모든 기능을 즐길 수 있어요!"
                SettingFinishState.LogoutFinish.value -> "다음에 또 다시 찾아주세요. \uD83D\uDE01"
                SettingFinishState.WithdrawFinish.value -> "그동안 이용해주셔서 감사합니다."
                else -> "설정 화면으로 이동해주세요."
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 32.dpToSp(),
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                color = Color.White,
                fontSize = 24.dpToSp(),
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )
        }
    }
}