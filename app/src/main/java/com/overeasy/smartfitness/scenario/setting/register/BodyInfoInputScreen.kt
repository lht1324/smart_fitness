package com.overeasy.smartfitness.scenario.setting.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.pxToDp
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun BodyInfoInputScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val spicyPreferenceList = remember {
        mutableStateListOf(
            "싫어해" to false,
            "1단계" to false,
            "2단계" to false,
            "3단계" to false,
            "4단계" to false
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
            "신맛" to false,
            "쓴맛" to false,
            "감칠맛" to false,
            "지방맛" to false,
        )
    }
    val activityPreferenceList = remember {
        mutableStateListOf(
            "응" to false,
            "아니" to false,
        )
    }
    val foodPreferenceList = remember {
        mutableStateListOf(
            "한식" to false,
            "중식" to false,
            "일식" to false,
            "양식" to false,
            "패스트푸드" to false,
            "디저트" to false,
        )
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .verticalScroll(state = scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            InputSection(
                title = "선호하는 매운 맛 단계를 골라주세요.",
                radioItemList = spicyPreferenceList,
                onClickItem = { selectedIndex ->
                    spicyPreferenceList.toList().forEachIndexed { index, (option, _) ->
                        spicyPreferenceList[index] = option to (index == selectedIndex)
                    }
                }
            )
            Separator()
            InputSection(
                title = "고기를 드시나요?",
                radioItemList = meatPreferenceList,
                onClickItem = { selectedIndex ->
                    meatPreferenceList.toList().forEachIndexed { index, (option, _) ->
                        meatPreferenceList[index] = option to (index == selectedIndex)
                    }
                }
            )
            Separator()
            InputSection(
                title = "선호하는 맛을 골라주세요.\n(최소 1개 이상, 복수 선택 가능)",
                radioItemList = tastePreferenceList,
                onClickItem = { selectedIndex ->
                    val (option, isSelected) = tastePreferenceList[selectedIndex]

                    tastePreferenceList[selectedIndex] = option to !isSelected
                }
            )
            Separator()
            InputSection(
                title = "활동적이신 분인가요?",
                radioItemList = activityPreferenceList,
                onClickItem = { selectedIndex ->
                    activityPreferenceList.toList().forEachIndexed { index, (option, _) ->
                        activityPreferenceList[index] = option to (index == selectedIndex)
                    }
                }
            )
            Separator()
            InputSection(
                title = "선호하는 음식 종류를 골라주세요.\n(최소 1개 이상, 복수 선택 가능)",
                radioItemList = foodPreferenceList,
                onClickItem = { selectedIndex ->
                    val (option, isSelected) = foodPreferenceList[selectedIndex]

                    foodPreferenceList[selectedIndex] = option to !isSelected
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

                    }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    text = "건너뛰기",
                    onClick = {

                    }
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
                    text = "취향 입력이 실패했어요.",
                    fontSize = 20.dpToSp(),
                    fontWeight = FontWeight.Light,
                    fontFamily = fontFamily
                )
            },
            text = {
                Text(
                    text = "모든 항목을 1개 이상 골라야 해요.",
                    fontSize = 18.dpToSp(),
                    fontWeight = FontWeight.Light,
                    fontFamily = fontFamily
                )
            }
        )
    }
}

@Composable
private fun InputSection(
    modifier: Modifier = Modifier,
    title: String,
    radioItemList: List<Pair<String, Boolean>>,
    onClickItem: (Int) -> Unit
) {
    val context = LocalContext.current

    var longestTextWidth by remember { mutableIntStateOf(0) }
    val longestTextWidthDp by remember {
        derivedStateOf {
            context.pxToDp(longestTextWidth)
        }
    }
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                radioItemList.forEachIndexed { index, (radioItem, isSelected) ->
                    Column(
                        modifier = Modifier
                            .then(
                                if (longestTextWidthDp != 0f) {
                                    Modifier // .width(longestTextWidthDp.dp)
                                } else {
                                    Modifier
                                }
                            )
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
                            }
                        )
                        Text(
                            text = radioItem,
                            modifier = Modifier.onSizeChanged { (width, _) ->
                                if (width > longestTextWidth)
                                    longestTextWidth = width
                            },
                            color = Color.White,
                            fontSize = 16.dpToSp(),
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = fontFamily
                        )
                    }
                    if (index != radioItemList.size - 1) {
                        Spacer(modifier = Modifier.width(15.dp))
                    }
                }
            }
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