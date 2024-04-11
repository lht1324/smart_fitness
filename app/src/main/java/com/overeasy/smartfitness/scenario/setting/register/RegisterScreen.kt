package com.overeasy.smartfitness.scenario.setting.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isShowFinishDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .noRippleClickable {
                isShowFinishDialog = !isShowFinishDialog
            }
            .background(color = ColorPrimary)
    ) {
        Row(
            modifier = Modifier.padding(top = 20.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                TextField(
                    value = id,
                    onValueChange = { value ->
                        id = value
                    },
                    placeholder = {
                        Text(
                            text = "내 정보",
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal,
                            fontFamily = fontFamily
                        )
                    },
                    shape = AbsoluteRoundedCornerShape(10.dp)
                )
//                Spacer(modifier = Modifier.height(20.dp))
//                TextField(
//                    value = password,
//                    onValueChange = { value ->
//                        password = value
//                    },
//                    placeholder = {
//                        Text(
//                            text = "비밀번호",
//                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
//                            color = Color.Gray,
//                            fontWeight = FontWeight.Normal,
//                            fontFamily = fontFamily
//                        )
//                    },
//                    shape = AbsoluteRoundedCornerShape(10.dp)
//                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(
                        color = ColorSecondary,
                        shape = AbsoluteRoundedCornerShape(10.dp)
                    )
            ) {
                Text(
                    text = "완료",
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
            }
        }
    }
    if (isShowFinishDialog) {
        AlertDialog(
            onDismissRequest = {
                isShowFinishDialog = false
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clickable {
                            isShowFinishDialog = false
                        }
                        .background(
                            color = Color.Transparent,
                            shape = AbsoluteRoundedCornerShape(10.dp)
                        )
                ) {
                    Text(
                        text = "다시 작성하기",
                        modifier = Modifier.padding(5.dp),
                        fontSize = 16.dpToSp(),
                        fontWeight = FontWeight.Light,
                        fontFamily = fontFamily
                    )
                }
            },
            title = {
                Text(
                    text = "닉네임 생성이 실패했어요.",
                    color = Color.Red,
                    fontSize = 20.dpToSp(),
                    fontWeight = FontWeight.Light,
                    fontFamily = fontFamily
                )
//                Text(
//                    text = "아이디를 잘못 입력했어요.",
//                    color = Color.Red,
//                    fontSize = 20.dpToSp(),
//                    fontWeight = FontWeight.Light,
//                    fontFamily = fontFamily
//                )
//                Text(
//                    text = "비밀번호를 잘못 입력했어요.",
//                    color = Color.Red,
//                    fontSize = 20.dpToSp(),
//                    fontWeight = FontWeight.Light,
//                    fontFamily = fontFamily
//                )
            },
            text = {
                Text(
                    text = "닉네임은 8자 이하여야 해요.",
                    fontSize = 20.dpToSp(),
                    fontWeight = FontWeight.Light,
                    fontFamily = fontFamily
                )
//                Text(
//                    text = "아이디, 비밀번호를 다시 확인해주세요.",
//                    fontSize = 20.dpToSp(),
//                    fontWeight = FontWeight.Light,
//                    fontFamily = fontFamily
//                )
//                Text(
//                    text = "아이디는 영문, 숫자를 섞어 15자 이하로 쓰셔야 해요.",
//                    fontSize = 18.dpToSp(),
//                    fontWeight = FontWeight.Light,
//                    fontFamily = fontFamily
//                )
//                Text(
//                    text = "비밀번호는 영문, 숫자를 섞어 8자 이상 쓰셔야 해요.",
//                    fontSize = 18.dpToSp(),
//                    fontWeight = FontWeight.Light,
//                    fontFamily = fontFamily
//                )
            }
        )
    }
}