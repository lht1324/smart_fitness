package com.overeasy.smartfitness.scenario.setting.myinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.setting.register.BodyInfoInputArea
import com.overeasy.smartfitness.scenario.setting.register.NicknameInputArea
import com.overeasy.smartfitness.scenario.setting.register.TasteInfoInputArea
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.fontFamily
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyInfoScreen(
    modifier: Modifier = Modifier,
    viewModel: MyInfoViewModel = hiltViewModel()
) {
    var isShowDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val nickname by viewModel.nickname.collectAsState()

    val bodyInfo by viewModel.bodyInfo.collectAsState()

    val tasteInfo by viewModel.tasteInfo.collectAsState()

    val isNicknameInvalid by viewModel.isNicknameInvalid.collectAsState(initial = false)

    val isAgeInvalid by viewModel.isAgeInvalid.collectAsState(initial = false)
    val isHeightInvalid by viewModel.isHeightInvalid.collectAsState(initial = false)
    val isWeightInvalid by viewModel.isWeightInvalid.collectAsState(initial = false)

    var isNicknameChanged by remember { mutableStateOf(false) }
    var isBodyInfoChanged by remember { mutableStateOf(false) }
    var isTasteInfoChanged by remember { mutableStateOf(false) }

    var dialogTitle by remember { mutableStateOf("") }
    var dialogDescription by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .verticalScroll(state = scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle(
                text = "닉네임"
            )
            NicknameInputArea(
                nickname = nickname,
                onChangeNickname = { value ->
                    if (!isNicknameChanged)
                        isNicknameChanged = true

                    viewModel.onChangeNickname(value)
                },
                isNicknameInvalid = isNicknameInvalid,
                buttonText = "변경하기",
                onFinish = {
                    if (isNicknameChanged) {
                        dialogTitle = "닉네임이 변경되었어요!"
                        viewModel.onClickChangeNickname()
                    }
                }
            )
//            SectionDivider(modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(modifier = Modifier.height(10.dp))
            SectionTitle(
                text = "신체 정보"
            )
            BodyInfoInputArea(
                age = bodyInfo.age ?: "",
                height = bodyInfo.height ?: "",
                weight = bodyInfo.weight ?: "",
                selectedGenderIndex = when (bodyInfo.gender) {
                    "male" -> 0
                    "female" -> 1
                    else -> null
                }.apply {
                    println("jaehoLee", "gender = ${bodyInfo.gender}, $this")
                },
                isAgeInvalid = isAgeInvalid,
                isHeightInvalid = isHeightInvalid,
                isWeightInvalid = isWeightInvalid,
                isInRegister = false,
                buttonText = "신체 정보\n변경하기",
                onChangeAge = { value ->
                    if (!isBodyInfoChanged)
                        isBodyInfoChanged = true

                    viewModel.onChangeAge(value)
                },
                onChangeHeight = { value ->
                    if (!isBodyInfoChanged)
                        isBodyInfoChanged = true

                    viewModel.onChangeHeight(value)
                },
                onChangeWeight = { value ->
                    if (!isBodyInfoChanged)
                        isBodyInfoChanged = true

                    viewModel.onChangeWeight(value)
                },
                onChangeGender = { selectedIndex ->
                    if (!isBodyInfoChanged)
                        isBodyInfoChanged = true

                    viewModel.onChangeGender(selectedIndex)
                },
                onClickFinish = {
                    if (isBodyInfoChanged) {
                        dialogTitle = "신체 정보가 변경되었어요!"
                        viewModel.onClickChangeBodyInfo()
                    }
                }
            )
//            SectionDivider(modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(modifier = Modifier.height(10.dp))
            SectionTitle(
                text = "취향"
            )
            TasteInfoInputArea(
                spicyPreference = tasteInfo.spicyPreference,
                meatConsumption = tasteInfo.meatConsumption,
                tastePreference = tasteInfo.tastePreference,
                activityLevel = tasteInfo.activityLevel,
                preferenceTypeFood = tasteInfo.preferenceTypeFood,
                isInRegister = false,
                onChangeSpicyPreference = { value ->
                    if (!isTasteInfoChanged)
                        isTasteInfoChanged = true

                    viewModel.onChangeSpicyPreference(value)
                },
                onChangeMeatConsumption = { value ->
                    if (!isTasteInfoChanged)
                        isTasteInfoChanged = true

                    viewModel.onChangeMeatConsumption(value)
                },
                onChangeTastePreference = { value ->
                    if (!isTasteInfoChanged)
                        isTasteInfoChanged = true

                    viewModel.onChangeTastePreference(value)
                },
                onChangeActivityLevel = { value ->
                    if (!isTasteInfoChanged)
                        isTasteInfoChanged = true

                    viewModel.onChangeActivityLevel(value)
                },
                onChangePreferenceTypeFood = { value ->
                    if (!isTasteInfoChanged)
                        isTasteInfoChanged = true

                    viewModel.onChangePreferenceTypeFood(value)
                },
                onFinishTasteInfoInput = {
                    if (isTasteInfoChanged) {
                        dialogTitle = "취향 정보가 변경되었어요!"
                        viewModel.onClickChangeTasteInfo()
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (isShowDialog) {
        Dialog(
            title = dialogTitle,
            description = dialogDescription,
            confirmText = "확인",
            onClickConfirm = {
                isShowDialog = false
            },
            onDismissRequest = {
                isShowDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.myInfoUiEvent.collectLatest { event ->
            when (event) {
                MyInfoViewModel.MyInfoUiEvent.OnSuccessChangeInfo -> {
                    isShowDialog = true
                }

                MyInfoViewModel.MyInfoUiEvent.OnFailureChangeInfo -> {
                    dialogTitle = "정보 변경이 실패했어요."
                    dialogDescription = "다시 한 번 시도해주세요."
                    isShowDialog = true
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = text,
            modifier = modifier,
            color = Color.White,
            fontSize = 24.dpToSp(),
            fontWeight = FontWeight.SemiBold,
            fontFamily = fontFamily
        )
        SectionDivider()
    }
}

@Composable
private fun SectionDivider(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(5.dp))
    }
}