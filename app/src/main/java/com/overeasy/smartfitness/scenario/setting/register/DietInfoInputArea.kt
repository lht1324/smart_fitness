@file:OptIn(ExperimentalLayoutApi::class)

package com.overeasy.smartfitness.scenario.setting.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.setting.public.SettingButton
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun DietInfoInputArea(
    modifier: Modifier = Modifier,
    menuList: List<String>,
    preferenceFoods: String?,
    onChangePreferenceFoods: (String) -> Unit,
    onFinishRegister: () -> Unit
) {
    val scrollState = rememberScrollState()

    var isShowUnfinishedDialog by remember { mutableStateOf(false) }

    val menuSelectStateList = remember { mutableStateListOf<Boolean>() }

    val isAllChecked by remember {
        derivedStateOf {
            if (menuSelectStateList.isNotEmpty()) {
                menuSelectStateList.all { isSelected ->
                    isSelected
                }
            } else {
                false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "좋아하는 메뉴를 원하시는 만큼 선택해주세요.\n(5개 이상)",
                color = Color.White,
                fontSize = 20.dpToSp(),
                fontWeight = FontWeight.ExtraBold,
                fontFamily = fontFamily
            )
            Separator()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CheckBox(
                    text = "전체 선택",
                    isChecked = isAllChecked,
                    onClick = {
                        val currentIsAllChecked = isAllChecked

                        menuSelectStateList.clear()
                        menuSelectStateList.addAll(
                            menuList.map { !currentIsAllChecked }
                        ).apply {
                            onChangePreferenceFoods(
                                if (this) {
                                    menuList.joinToString(",") { menu ->
                                        menu
                                    }
                                } else {
                                    ""
                                }
                            )
                        }
                    }
                )
                SettingButton(
                    text = "완료",
                    onClick = {
                        if (menuSelectStateList.count { isSelected -> isSelected } >= 5) {
                            onFinishRegister()
                        } else {
                            isShowUnfinishedDialog = true
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            FlowRow(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (menuList.isNotEmpty() && menuSelectStateList.isNotEmpty() && menuList.size == menuSelectStateList.size) {
                    menuList.forEachIndexed { index, menu ->
                        MenuItem(
                            menu = menu,
                            isSelected = menuSelectStateList[index],
                            onClick = {
                                menuSelectStateList[index] = !menuSelectStateList[index]
                                onChangePreferenceFoods(
                                    menuList.filterIndexed { index, _ ->
                                        menuSelectStateList[index]
                                    }.joinToString(",") { menu ->
                                        menu
                                    }
                                )
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (isShowUnfinishedDialog) {
        Dialog(
            title = "메뉴 선택이 실패했어요.",
            description = "메뉴를 5개 이상 골라야 해요.",
            confirmText = "다시 고르기",
            onClickConfirm = {
                isShowUnfinishedDialog = false
            },
            onDismissRequest = {
                isShowUnfinishedDialog = false
            }
        )
    }

    LaunchedEffect(menuList) {
        if (menuList.isNotEmpty()) {
            val preferenceFoodList = preferenceFoods?.split(',')

            menuSelectStateList.addAll(
                menuList.map { menu ->
                    preferenceFoodList?.contains(menu) == true
                }
            )
        }
    }
}

@Composable
private fun CheckBox(
    modifier: Modifier = Modifier,
    text: String = "",
    isChecked: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.noRippleClickable {
            onClick()
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = null
        )
        if (text.isNotEmpty()) {
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.dpToSp(),
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily
            )
        }
    }
}

@Composable
private fun MenuItem(
    modifier: Modifier = Modifier,
    menu: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isSelected) {
                        Color.LightGray
                    } else {
                        ColorPrimary
                    },
                    shape = AbsoluteRoundedCornerShape(5.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = AbsoluteRoundedCornerShape(5.dp)
                )
                .noRippleClickable {
                    onClick()
                }
        ) {
            Text(
                text = menu,
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                color = if (isSelected) {
                    ColorPrimary
                } else {
                    Color.LightGray
                },
                fontSize = 16.dpToSp(),
                fontWeight = FontWeight.Medium,
                fontFamily = fontFamily
            )
        }
    }
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