package com.overeasy.smartfitness.scenario.setting.myinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.scenario.setting.public.SettingTextField
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun MyInfoScreen(
    modifier: Modifier = Modifier,
    viewModel: MyInfoViewModel = hiltViewModel()
) {
    var isShowFinishDialog by remember { mutableStateOf(false) }

    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingTextField(
                        value = age,
                        onValueChange = { value ->
                            if (value.toIntOrNull() != null || value.isEmpty()) {
                                age = value
                            }
                        },
                        placeholder = "나이"
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "세",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingTextField(
                        value = height,
                        onValueChange = { value ->
                            if (value.toIntOrNull() != null || value.toFloatOrNull() != null || value.isEmpty()) {
                                height = value
                            }
                        },
                        placeholder = "키"
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "cm",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingTextField(
                        value = weight,
                        onValueChange = { value ->
                            if (value.toIntOrNull() != null || value.toFloatOrNull() != null || value.isEmpty()) {
                                weight = value
                            }
                        },
                        placeholder = "몸무게"
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "kg",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = ColorSecondary,
                            shape = AbsoluteRoundedCornerShape(10.dp)
                        )
                        .clickable {
                            isShowFinishDialog = true
                        }
//                    .align(Alignment.End)
                ) {
                    Text(
                        text = "신체 정보\n입력하기",
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
                        color = Color.White,
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = ColorSecondary,
                            shape = AbsoluteRoundedCornerShape(10.dp)
                        )
                        .clickable {
                            isShowFinishDialog = true
                        }
//                    .align(Alignment.End)
                ) {
                    Text(
                        text = "건너뛰기",
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
                        color = Color.White,
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (true) {
            AlertDialog(
                onDismissRequest = {
//                isShowFinishDialog = false
                },
                confirmButton = {
                    Box(
                        modifier = Modifier
                            .clickable {
//                                finish()
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
                        text = "신체 정보 입력이 실패했어요.",
                        fontSize = 20.dpToSp(),
                        fontWeight = FontWeight.Light,
                        fontFamily = fontFamily
                    )
                },
                text = {
                    Text(
                        text = "키, 몸무게, 나이를 전부 입력해야 해요.",
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.Light,
                        fontFamily = fontFamily
                    )
                }
            )
        }
//        Spacer(modifier = Modifier.height(20.dp))
//        Column(
//            modifier = Modifier.padding(horizontal = 20.dp)
//        ) {
//            Text(
//                text = "닉네임",
//                color = Color.White,
//                fontSize = 16.dpToSp(),
//                fontWeight = FontWeight.Medium,
//                fontFamily = fontFamily
//            )
//            Spacer(modifier = Modifier.height(5.dp))
//            Text(
//                text = "* 닉네임 변경은 60일마다 한 번 가능합니다.",
//                color = Color.Gray,
//                fontSize = 14.dpToSp(),
//                fontWeight = FontWeight.Medium,
//                fontFamily = fontFamily
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                SettingTextField(
//                    value = nickname,
//                    onValueChange = { value ->
//                        nickname = value
//                    },
//                    placeholder = "닉네임"
//                )
//                Spacer(modifier = Modifier.width(10.dp))
//                Box(
//                    modifier = Modifier
//                        .padding(end = 20.dp)
//                        .background(
//                            color = ColorSecondary,
//                            shape = AbsoluteRoundedCornerShape(10.dp)
//                        )
//                        .clickable {
//                            isShowFinishDialog = true
//                        }
//                ) {
//                    Text(
//                        text = "변경하기",
//                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
//                        color = Color.White,
//                        fontSize = 18.dpToSp(),
//                        fontWeight = FontWeight.Bold,
//                        fontFamily = fontFamily,
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//        }
    }
}