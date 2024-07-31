package com.overeasy.smartfitness.scenario.setting.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    isLogin: Boolean = false,
    onClickLogin: () -> Unit,
    onClickFindId: () -> Unit,
    onClickRegister: () -> Unit,
    onClickMyInfo: () -> Unit,
    onClickLogout: () -> Unit,
    onClickWithdraw: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingItem(
            text = if (isLogin) {
                "내 정보"
            } else {
                "로그인"
            },
            onClick = if (isLogin) {
                onClickMyInfo
            } else {
                onClickLogin
            }
        )
        SettingItem(
            text = if (isLogin) {
                "로그아웃"
            } else {
                "아이디 찾기"
            },
            onClick = if (isLogin) {
                onClickLogout
            } else {
                onClickFindId
            }
        )
        SettingItem(
            text = if (isLogin) {
                "탈퇴"
            } else {
                "회원가입"
            },
            onClick = if (isLogin) {
                onClickWithdraw
            } else {
                onClickRegister
            },
            textColor = if (isLogin) {
                Color.Red
            } else {
                Color.White
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "문의: gunoh928na@naver.com",
            color = Color.LightGray,
            fontSize = 16.dpToSp(),
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SettingItem(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    textColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterStart),
            color = textColor,
            fontSize = 24.dpToSp(),
            fontWeight = FontWeight.ExtraBold,
            fontFamily = fontFamily
        )
    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp),
        color = ColorSecondary
    )
}