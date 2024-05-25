@file:OptIn(ExperimentalLayoutApi::class)

package com.overeasy.smartfitness.scenario.workout.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.R
import com.overeasy.smartfitness.domain.workout.model.workout.WorkoutData
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.pxToDp
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.fontFamily
import kotlinx.coroutines.launch

@Composable
fun WorkoutInfoInputDialog(
    modifier: Modifier = Modifier,
    workoutNameList: List<String>,
    isImeVisible: Boolean,
    onClickWatchExampleVideo: (String) -> Unit,
    onFinish: (WorkoutInfo) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var isShowInvalidDialog by remember { mutableStateOf(false) }

    var selectedWorkoutNameIndex by remember { mutableIntStateOf(0) }

    var workoutInfo by remember {
        mutableStateOf(
            WorkoutInfo(
                workoutName = workoutNameList.firstOrNull() ?: "",
                restTime = 30,
                // weight to count
                setDataList = listOf(
                    WorkoutData(
                        setNum = 1,
                        repeats = 10,
                        weight = 10
                    )
                )
            )
        )
    } // 임시

    var buttonAreaWidth by remember { mutableFloatStateOf(0f) }
    var buttonAreaHeight by remember { mutableIntStateOf(0) }
    var contentContainerHeight by remember { mutableIntStateOf(0) }

    var setDataListSize by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .onSizeChanged { (_, height) ->
                    if (height != contentContainerHeight) {
                        contentContainerHeight = height
                    }
                }
                .verticalScroll(state = scrollState)
        ) {
            WorkoutInfoInputSection(
                title = "운동 선택",
                content = {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
                    ) {
                        workoutNameList.forEachIndexed { index, workoutName ->
                            WorkoutName(
                                modifier = Modifier.noRippleClickable {
                                    if (selectedWorkoutNameIndex != index) {
                                        selectedWorkoutNameIndex = index

                                        workoutInfo = workoutInfo.copy(
                                            workoutName = workoutName
                                        )
                                    }
                                },
                                workoutName = workoutName,
                                isSelected = index == selectedWorkoutNameIndex
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (workoutNameList.isNotEmpty()) {
                        Text(
                            text = "${workoutNameList[selectedWorkoutNameIndex]} 예시 영상 조회",
                            modifier = Modifier
                                .noRippleClickable {
                                    onClickWatchExampleVideo(workoutNameList[selectedWorkoutNameIndex])
                                },
                            color = ColorSaturday,
                            fontSize = 18.dpToSp(),
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = fontFamily,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            )
            Divider()
            WorkoutInfoInputSection(
                title = "휴식 시간",
                content = {
                    SetDataItemTextField(
                        value = workoutInfo.restTime?.toString() ?: "",
                        onValueChange = { value ->
                            workoutInfo = workoutInfo.copy(
                                restTime = value.toIntOrNull()
                            )
                        },
                        unitText = "초"
                    )
                }
            )
            Divider()
            WorkoutInfoInputSection(
                title = "세트 정보",
                content = {
                    workoutInfo.setDataList.forEachIndexed { index, setData ->
                        SetDataItem(
                            set = index + 1,
                            weight = setData.weight,
                            count = setData.repeats,
                            onChangeWeight = { value ->
                                workoutInfo = workoutInfo.copy(
                                    setDataList = workoutInfo.setDataList
                                        .mapIndexed { setDataIndex, setData ->
                                            setData.copy(
                                                weight = if (index == setDataIndex) {
                                                    if (value.isNotEmpty())
                                                        value.toInt()
                                                    else
                                                        null
                                                } else {
                                                    setData.weight
                                                }
                                            )
                                        }
                                )
                            },
                            onChangeCount = { value ->
                                workoutInfo = workoutInfo.copy(
                                    setDataList = workoutInfo.setDataList
                                        .mapIndexed { setDataIndex, setData ->
                                            setData.copy(
                                                repeats = if (index == setDataIndex) {
                                                    if (value.isNotEmpty())
                                                        value.toInt()
                                                    else
                                                        null
                                                } else {
                                                    setData.repeats
                                                }
                                            )
                                        }
                                )
                            },
                            onClickDelete = {
                                workoutInfo = workoutInfo.copy(
                                    setDataList = workoutInfo.setDataList.filterIndexed { setDataIndex, _ ->
                                        setDataIndex != index
                                    }
                                )
                                focusManager.clearFocus()
                            }
                        )
                        if (index != workoutInfo.setDataList.size - 1) {
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height((context.pxToDp(buttonAreaHeight).dp)))
        }
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .align(Alignment.BottomCenter)
                .onSizeChanged { (_, height) ->
                    if (height != buttonAreaHeight) {
                        buttonAreaHeight = height
                    }
                }
        ) {
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { (width, _) ->
                        if (width.toFloat() != buttonAreaWidth) {
                            buttonAreaWidth = width.toFloat()
                        }
                    },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier.width(context.pxToDp((buttonAreaWidth * 0.475f).toInt()).dp),
                    text = "세트 추가",
                    onClick = {
                        workoutInfo = workoutInfo.copy(
                            setDataList = if (workoutInfo.setDataList.isNotEmpty()) {
                                workoutInfo.setDataList + workoutInfo.setDataList.last()
                            } else {
                                listOf(
                                    WorkoutData(
                                        setNum = 1,
                                        weight = 10,
                                        repeats = 10
                                    )
                                )
                            }
                        )
                        focusManager.clearFocus()
                    }
                )
                Button(
                    modifier = Modifier.width(context.pxToDp((buttonAreaWidth * 0.475f).toInt()).dp),
                    text = "완료",
                    onClick = {
                        val isSetDataListInvalid = workoutInfo.setDataList.any { (weight, count) ->
                            weight == null || weight == 0 || count == null || count == 0
                        } || workoutInfo.setDataList.isEmpty()
                        val isRestTimeInvalid = workoutInfo.restTime == 0 || workoutInfo.restTime == null

                        val isWorkoutInfoInvalid = isSetDataListInvalid || isRestTimeInvalid

                        if (isWorkoutInfoInvalid) {
                            isShowInvalidDialog = true
                        } else {
                            onFinish(workoutInfo)
                        }
                        focusManager.clearFocus()
                    }
                )
            }
        }

        if (isShowInvalidDialog) {
            Dialog(
                title = "운동을 시작할 수 없어요.",
                description = "빈 부분을 채우거나 0을 바꾸고\n다시 완료 버튼을 눌러주세요.",
                confirmText = "다시 작성하기",
                onClickConfirm = {
                    isShowInvalidDialog = false
                },
                onDismissRequest = {
                    isShowInvalidDialog = false
                }
            )
        }
    }

    LaunchedEffect(workoutInfo.setDataList.size) {
        if (setDataListSize != workoutInfo.setDataList.size) {
            setDataListSize = workoutInfo.setDataList.size

            coroutineScope.launch {
                scrollState.scrollBy(contentContainerHeight.toFloat())
            }
        }
    }

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible)
            focusManager.clearFocus()
    }
}

@Composable
private fun WorkoutInfoInputSection(
    modifier: Modifier = Modifier,
    title: String = "",
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 20.dpToSp(),
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        content()
    }
}

@Composable
private fun WorkoutName(
    modifier: Modifier = Modifier,
    workoutName: String,
    isSelected: Boolean
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) {
                    Color.LightGray
                } else {
                    ColorPrimary
                },
                shape = AbsoluteRoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = AbsoluteRoundedCornerShape(10.dp)
            )
    ) {
        Text(
            text = workoutName,
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
            color = if (isSelected) {
                ColorPrimary
            } else {
                Color.LightGray
            },
            fontSize = 16.dpToSp(),
            fontWeight = FontWeight.Light,
            fontFamily = fontFamily
        )
    }
}

@Composable
private fun SetDataItem(
    modifier: Modifier = Modifier,
    set: Int,
    weight: Int?,
    count: Int?,
    onChangeWeight: (String) -> Unit,
    onChangeCount: (String) -> Unit,
    onClickDelete: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = ColorPrimary,
                shape = AbsoluteRoundedCornerShape(5.dp)
            )
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = AbsoluteRoundedCornerShape(5.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${set}세트",
                color = Color.LightGray,
                fontSize = 16.dpToSp(),
                fontWeight = FontWeight.Medium,
                fontFamily = fontFamily
            )
//            Spacer(modifier = Modifier.width(10.dp))
//            Text(
//                text = "${weight}kg",
//                color = Color.LightGray,
//                fontSize = 16.dpToSp(),
//                fontWeight = FontWeight.Medium,
//                fontFamily = fontFamily
//            )
//            Spacer(modifier = Modifier.width(10.dp))
            SetDataItemTextField(
                value = weight?.toString() ?: "",
                onValueChange = onChangeWeight,
                unitText = "kg"
            )
            SetDataItemTextField(
                value = count?.toString() ?: "",
                onValueChange = onChangeCount,
                unitText = "회"
            )
//            Text(
//                text = "${count}회",
//                color = Color.LightGray,
//                fontSize = 16.dpToSp(),
//                fontWeight = FontWeight.Medium,
//                fontFamily = fontFamily
//            )
//            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.ic_delete),
                modifier = Modifier
                    .size(24.dp)
                    .noRippleClickable {
                        onClickDelete()
                    },
                contentDescription = null
            )
        }
    }
}

@Composable
private fun Button(
    modifier: Modifier = Modifier,
    text: String = "",
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = ColorPrimary,
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
            text = text,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .align(Alignment.Center),
            color = Color.LightGray,
            fontSize = 18.dpToSp(),
            fontWeight = FontWeight.SemiBold,
            fontFamily = fontFamily
        )
    }
}

@Composable
private fun Divider(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}