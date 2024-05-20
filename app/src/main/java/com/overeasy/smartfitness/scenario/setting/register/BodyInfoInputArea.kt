package com.overeasy.smartfitness.scenario.setting.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.pxToDp
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.setting.public.SettingButton
import com.overeasy.smartfitness.scenario.setting.public.SettingTextField
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun BodyInfoInputArea(
    modifier: Modifier = Modifier,
    age: String,
    height: String,
    weight: String,
    selectedGenderIndex: Int? = null,
    isAgeInvalid: Boolean,
    isHeightInvalid: Boolean,
    isWeightInvalid: Boolean,
    isInRegister: Boolean = true,
    buttonText: String = "완료",
    onChangeAge: (String) -> Unit,
    onChangeHeight: (String) -> Unit,
    onChangeWeight: (String) -> Unit,
    onChangeGender: (Int) -> Unit,
    onClickFinish: () -> Unit
) {
    val context = LocalContext.current

    var isShowSkipDialog by remember { mutableStateOf(false) }
    var isShowUnfinishedDialog by remember { mutableStateOf(false) }

    var inputButtonHeightPx by remember { mutableIntStateOf(0) }
    val inputButtonHeightDp by remember {
        derivedStateOf {
            context.pxToDp(inputButtonHeightPx)
        }
    }

    val genderList = remember {
        mutableStateListOf(
            "남자" to false,
            "여자" to false
        )
    }

    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        BodyInfoInput(
            value = age,
            onValueChange = onChangeAge,
            placeholder = "나이",
            metric = "세",
            isInvalid = isAgeInvalid,
            invalidText = "100세 미만의 나이만 입력할 수 있어요."
        )
        BodyInfoInput(
            value = height,
            onValueChange = onChangeHeight,
            placeholder = "키",
            metric = "cm",
            isInvalid = isHeightInvalid,
            invalidText = "120~250cm 사이의 키만 입력할 수 있어요."
        )
        BodyInfoInput(
            value = weight,
            onValueChange = onChangeWeight,
            placeholder = "몸무게",
            metric = "kg",
            isInvalid = isWeightInvalid,
            invalidText = "30~200kg 사이의 몸무게만 입력할 수 있어요."
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "성별",
                color = Color.White,
                fontSize = 18.dpToSp(),
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(10.dp))
            RadioList(
                radioItemList = genderList,
                onClickItem = { selectedIndex ->
                    genderList.toList().forEachIndexed { index, (gender, _) ->
                        genderList[index] = gender to (index == selectedIndex)
                    }
                    onChangeGender(selectedIndex)
                }
            )
        }

        SettingButton(
            modifier = Modifier
                .onSizeChanged { (_, height) ->
                    if (inputButtonHeightPx != height)
                        inputButtonHeightPx = height
                }
                .align(Alignment.End),
            text = buttonText,
            onClick = {
                val isEveryBodyInfoValid = !isAgeInvalid && !isHeightInvalid && !isWeightInvalid
                val isEveryBodyInfoNotEmpty = age.isNotEmpty() && height.isNotEmpty() && weight.isNotEmpty()

                if (isEveryBodyInfoValid && isEveryBodyInfoNotEmpty) {
                    onClickFinish()
                } else {
                    isShowUnfinishedDialog = true
                }
            }
        )
    }

    if (isShowSkipDialog) {
        Dialog(
            title = "귀찮으세요?",
            description = "로그인한 뒤 설정에서 내 정보에 들어가면 다시 작성하실 수 있어요!",
            confirmText = "취소",
            dismissText = "건너뛰기",
            onClickConfirm = {
                isShowSkipDialog = false
            },
            onClickDismiss = onClickFinish,
            onDismissRequest = {
                isShowSkipDialog = false
            }
        )
    }

    if (isShowUnfinishedDialog) {
        Dialog(
            title = "입력하지 않았거나 잘못 입력한 신체 정보가 있네요.",
            description = "모든 항목을 제대로 입력해야 해요.",
            confirmText = "다시 작성하기",
            onClickConfirm = {
                isShowUnfinishedDialog = false
            },
            onDismissRequest = {
                isShowUnfinishedDialog = false
            }
        )
    }

    LaunchedEffect(selectedGenderIndex) {
        genderList.toList().forEachIndexed { index, (gender, _) ->
            genderList[index] = gender to (selectedGenderIndex == index)
        }
    }
}

@Composable
private fun BodyInfoInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    metric: String, // 단위
    isInvalid: Boolean,
    invalidText: String = ""
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingTextField(
                modifier = Modifier.weight(0.9f),
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                isInvalid = isInvalid
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = metric,
                modifier = Modifier.weight(0.1f),
                color = Color.White,
                fontSize = 18.dpToSp(),
                fontWeight = FontWeight.Medium,
                fontFamily = fontFamily
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = invalidText,
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.Start),
            color = if (isInvalid) {
                Color.Red
            } else {
                Color.Transparent
            },
            fontSize = 16.dpToSp(),
            fontWeight = FontWeight.Medium,
            fontFamily = fontFamily,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Composable
private fun RadioList(
    modifier: Modifier = Modifier,
    radioItemList: List<Pair<String, Boolean>>,
    onClickItem: (Int) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier.horizontalScroll(state = scrollState),
        verticalAlignment = Alignment.CenterVertically
    ) {
        radioItemList.forEachIndexed { index, (radioItem, isSelected) ->
            Column(
                modifier = Modifier
                    .clickable {
                        onClickItem(index)
                    }
                    .background(
                        color = Color.Transparent,
                        shape = AbsoluteRoundedCornerShape(5.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = {
                        onClickItem(index)
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = ColorSecondary,
                        unselectedColor = ColorSecondary
                    )
                )
                Text(
                    text = radioItem,
                    color = Color.White,
                    fontSize = 16.dpToSp(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
            }
            if (index != radioItemList.size - 1) {
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}