@file:OptIn(ExperimentalMaterial3Api::class)

package com.overeasy.smartfitness.scenario.setting.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun TasteInfoInputArea(
    modifier: Modifier = Modifier,
    spicyPreference: Int?,
    meatConsumption: Boolean?,
    tastePreference: String?,
    activityLevel: Int?,
    preferenceTypeFood: String?,
    isInRegister: Boolean = true,
    onChangeSpicyPreference: (Int) -> Unit,
    onChangeMeatConsumption: (Boolean) -> Unit,
    onChangeTastePreference: (String) -> Unit,
    onChangeActivityLevel: (Int) -> Unit,
    onChangePreferenceTypeFood: (String) -> Unit,
    onClickSkipTasteInput: () -> Unit = { },
    onFinishTasteInfoInput: () -> Unit
) {
    var isShowUnfinishedDialog by remember { mutableStateOf(false) }
    var isShowNeedEssentialDialog by remember { mutableStateOf(false) }
    var isShowSkipDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val spicyPreferenceList = remember {
        mutableStateListOf(
            "안 매움" to false,
            "조금 매움" to false,
            "매움" to false,
            "아주 매움" to false
        )
    }
    val meatPreferenceList = remember {
        mutableStateListOf(
            "응" to false,
            "아니" to false,
        )
    }
    val tastePreferenceList = remember {
        mutableStateListOf(
            "단맛" to false,
            "짠맛" to false,
            "매운 맛" to false,
            "담백한 맛" to false,
            "느끼한 맛" to false,
            "상관 없음" to false,
        )
    }
    // title = "활동적이신 분인가요?",
    val activityPreferenceList = remember {
        mutableStateListOf(
            "의자와 한몸" to false,
            "많이는 아님" to false,
            "운동 좋아하는 사람" to false,
            "지치는 게 뭐지?" to false
        )
    }
    val foodPreferenceList = remember {
        mutableStateListOf(
            "한식" to false,
            "중식" to false,
            "일식" to false,
            "양식" to false
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Column(
            modifier = Modifier
                .then(
                    if (isInRegister) {
                        Modifier.verticalScroll(state = scrollState)
                    } else {
                        Modifier
                    }
                )
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "필수 입력",
                color = Color.White,
                fontSize = 20.dpToSp(),
                fontWeight = FontWeight.ExtraBold,
                fontFamily = fontFamily
            )
            Separator()
            InputSectionDropdown(
                title = "활동적이신 분인가요?",
                dropdownItemList = activityPreferenceList.toList(),
                onClickItem = { selectedIndex ->
                    activityPreferenceList.toList().forEachIndexed { index, _ ->
                        activityPreferenceList[index] = activityPreferenceList[index].copy(
                            second = index == selectedIndex
                        )
                    }
                    onChangeActivityLevel(selectedIndex)
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "선택 입력",
                color = Color.White,
                fontSize = 20.dpToSp(),
                fontWeight = FontWeight.ExtraBold,
                fontFamily = fontFamily
            )
            Separator()
            InputSectionDropdown(
                modifier = Modifier.wrapContentSize(),
                title = "선호하는 매운 맛 단계를 골라주세요.",
                dropdownItemList = spicyPreferenceList.toList(),
                onClickItem = { selectedIndex ->
                    spicyPreferenceList.toList().forEachIndexed { index, _ ->
                        spicyPreferenceList[index] = spicyPreferenceList[index].copy(
                            second = index == selectedIndex
                        )
                    }
                    onChangeSpicyPreference(selectedIndex)
                }
            )
            Separator()
            InputSectionDropdown(
                title = "고기를 드시나요?",
                dropdownItemList = meatPreferenceList.toList(),
                onClickItem = { selectedIndex ->
                    meatPreferenceList.toList().forEachIndexed { index, _ ->
                        meatPreferenceList[index] = meatPreferenceList[index].copy(
                            second = index == selectedIndex
                        )
                    }
                    onChangeMeatConsumption(selectedIndex == 0)
                }
            )
            Separator()
            InputSectionRadio(
                title = "선호하는 맛을 골라주세요.\n(최소 1개 이상, 복수 선택 가능)",
                radioItemList = tastePreferenceList.toList(),
                onClickItem = { selectedIndex ->
                    val (_, isSelected) = tastePreferenceList[selectedIndex]

                    tastePreferenceList[selectedIndex] = tastePreferenceList[selectedIndex].copy(
                        second = !(isSelected)
                    )

                    val result = tastePreferenceList.toList().filter { (_, isSelected) ->
                        isSelected
                    }.joinToString(",") { (name, _) ->
                        name
                    }

                    onChangeTastePreference(result)
                }
            )
            Separator()
            InputSectionRadio(
                title = "선호하는 음식 종류를 골라주세요.\n(최소 1개 이상, 복수 선택 가능)",
                radioItemList = foodPreferenceList.toList(),
                onClickItem = { selectedIndex ->
                    val (_, isSelected) = foodPreferenceList[selectedIndex]

                    foodPreferenceList[selectedIndex] = foodPreferenceList[selectedIndex].copy(
                        second = !isSelected
                    )

                    val result = foodPreferenceList.toList().filter { (_, isSelected) ->
                        isSelected
                    }.joinToString(",") { (name, _) ->
                        name
                    }

                    onChangePreferenceTypeFood(result)
                }
            )
            Separator()
            Row(
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Button(
                    text = "완료",
                    onClick = {
                        val isSpicyPreferenceChecked =
                            spicyPreferenceList.any { (_, isChecked) -> isChecked }
                        val isMeatPreferenceChecked =
                            meatPreferenceList.any { (_, isChecked) -> isChecked }
                        val isTastePreferenceChecked =
                            tastePreferenceList.any { (_, isChecked) -> isChecked }
                        val isActivityPreferenceChecked =
                            activityPreferenceList.any { (_, isChecked) -> isChecked }
                        val isFoodPreferenceChecked =
                            foodPreferenceList.any { (_, isChecked) -> isChecked }

                        val isEveryOptionSelected = isSpicyPreferenceChecked &&
                                isMeatPreferenceChecked &&
                                isTastePreferenceChecked &&
                                isActivityPreferenceChecked &&
                                isFoodPreferenceChecked

                        val isEveryOptionUnselected = !isSpicyPreferenceChecked &&
                                !isMeatPreferenceChecked &&
                                !isTastePreferenceChecked &&
                                !isActivityPreferenceChecked &&
                                !isFoodPreferenceChecked

                        val isEssentialOptionSelectedOnly = isActivityPreferenceChecked &&
                                !isSpicyPreferenceChecked &&
                                !isMeatPreferenceChecked &&
                                !isTastePreferenceChecked &&
                                !isFoodPreferenceChecked

                        if (isEveryOptionSelected || isEssentialOptionSelectedOnly) {
                            onFinishTasteInfoInput()
                        } else if (isEveryOptionUnselected) {
                            isShowNeedEssentialDialog = true
                        } else {
                            isShowUnfinishedDialog = true
                        }
                    }
                )
                if (isInRegister) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        text = "건너뛰기",
                        onClick = {
                            isShowSkipDialog = true
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (isShowNeedEssentialDialog) {
        Dialog(
            title = "취향 입력이 실패했어요.",
            description = "필수 항목은 전부 골라야 해요.",
            confirmText = "다시 작성하기",
            onClickConfirm = {
                isShowNeedEssentialDialog = false
            },
            onDismissRequest = {
                isShowNeedEssentialDialog = false
            }
        )
    }

    if (isShowUnfinishedDialog) {
        Dialog(
            title = "취향 입력이 실패했어요.",
            description = "모든 항목을 1개 이상 골라야 해요.",
            confirmText = "다시 작성하기",
            onClickConfirm = {
                isShowUnfinishedDialog = false
            },
            onDismissRequest = {
                isShowUnfinishedDialog = false
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
            onClickDismiss = {
                onClickSkipTasteInput()
                isShowSkipDialog = false
            },
            onDismissRequest = {
                isShowSkipDialog = false
            }
        )
    }

    LaunchedEffect(spicyPreference, meatConsumption, tastePreference, activityLevel, preferenceTypeFood) {
        if (spicyPreference != null) {
            val (name, _) = spicyPreferenceList[spicyPreference]

            spicyPreferenceList[spicyPreference] = name to true
        }
        if (meatConsumption != null) {
            if (meatConsumption) {
                meatPreferenceList[0] = meatPreferenceList[0].copy(second = true)
            } else {
                meatPreferenceList[1] = meatPreferenceList[1].copy(second = true)
            }
        }
        if (tastePreference != null) {
            val indexList = tastePreferenceList.toList().mapIndexed { index, (option, _) ->
                if (tastePreference.contains(option))
                    index
                else
                    -1
            }.filter { index ->
                index != -1
            }

            indexList.forEach { index ->
                tastePreferenceList[index] = tastePreferenceList[index].copy(
                    second = true
                )
            }
//            val index = tastePreferenceList.indexOfFirst { (name, _) ->
//                name == tastePreference
//            }
//
//            tastePreferenceList[index] = tastePreferenceList[index].copy(second = true)
        }
        if (activityLevel != null) {
            val (name, _) = activityPreferenceList[activityLevel]

            activityPreferenceList[activityLevel] = name to true
        }
        if (preferenceTypeFood != null) {
            val indexList = foodPreferenceList.toList().mapIndexed { index, (option, _) ->
                if (preferenceTypeFood.contains(option))
                    index
                else
                    -1
            }.filter { index ->
                index != -1
            }

            indexList.forEach { index ->
                foodPreferenceList[index] = foodPreferenceList[index].copy(
                    second = true
                )
            }
//            val index = foodPreferenceList.indexOfFirst { (name, _) ->
//                name == preferenceTypeFood
//            }
//
//            foodPreferenceList[index] = foodPreferenceList[index].copy(second = true)
        }
    }
}

@Composable
private fun InputSectionRadio(
    modifier: Modifier = Modifier,
    title: String,
    radioItemList: List<Pair<String, Boolean>>,
    onClickItem: (Int) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
    ) {
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.dpToSp(),
                fontWeight = FontWeight.ExtraBold,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.horizontalScroll(state = scrollState),
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
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = fontFamily
                        )
                    }
                    if (index != radioItemList.size - 1) {
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InputSectionDropdown(
    modifier: Modifier = Modifier,
    title: String,
    dropdownItemList: List<Pair<String, Boolean>>,
    onClickItem: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.dpToSp(),
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(5.dp))
            TasteDropdown(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
                dropdownItemList = dropdownItemList,
                onClickItem = { selectedIndex ->
                    onClickItem(selectedIndex)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun TasteDropdown(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    dropdownItemList: List<Pair<String, Boolean>>,
    onClickItem: (Int) -> Unit
) {
    var selectedText by remember { mutableStateOf("선택 안 함") }
    val designedModifier = Modifier
        .background(
            color = Color.White
        )
        .border(
            width = 2.dp,
            color = Color.LightGray
        )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
            .wrapContentSize()
    ) {
        TextField(
            value = selectedText,
            onValueChange = {},
            modifier = designedModifier.menuAnchor(),
            readOnly = true,
            textStyle = TextStyle(
                color = Color.LightGray,
                fontSize = 16.dpToSp(),
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily
            ),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            },
            modifier = designedModifier
        ) {
            dropdownItemList.forEachIndexed { index, (dropdownItemText, _) ->
                TasteDropdownItem(
                    text = dropdownItemText,
                    onClick = {
                        onClickItem(index)
                    }
                )
            }
        }
    }

    LaunchedEffect(dropdownItemList) {
        selectedText = dropdownItemList.run {
            find { (_, isSelected) ->
                isSelected
            }?.first ?: "선택 안 함"
        }
    }
}

@Composable
private fun TasteDropdownItem(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                color = Color.LightGray,
                fontSize = 16.dpToSp(),
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily
            )
        },
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun Separator(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Composable
private fun Button(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = ColorSecondary,
                shape = AbsoluteRoundedCornerShape(10.dp)
            )
            .clickable {
                onClick()
            }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontFamily = fontFamily
        )
    }
}