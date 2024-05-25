package com.overeasy.smartfitness.scenario.diet.diet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.R
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.scenario.diet.public.DietTextButton
import com.overeasy.smartfitness.scenario.diet.public.DietTextField
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DietScreen(
    modifier: Modifier = Modifier,
    viewModel: DietViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val scrollState = rememberScrollState()

    var isShowNotFinishDialog by remember { mutableStateOf(false) }
    var isShowAllEmptyDialog by remember { mutableStateOf(false) }

    val screenState by viewModel.screenState.collectAsState()

    val userMenuList = remember { viewModel.userMenuList }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        when (screenState) {
            DietScreenState.NEED_LOGIN -> {
                Text(
                    text = "로그인이 필요합니다.",
                    modifier = Modifier.align(Alignment.Center),
                    color = ColorSecondary,
                    fontSize = 24.dpToSp(),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center
                )
            }
            DietScreenState.NORMAL -> {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxSize()
                        .verticalScroll(state = scrollState)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    DietTextButton(
                        modifier = Modifier
//                            .padding(end = (10 + 36).dp)
                            .align(Alignment.End),
                        text = "입력 완료",
                        onClick = {
                            if (!(userMenuList.all { userMenu -> userMenu.isEmpty() })) {
                                viewModel.onClickFinishInput()
                            } else {
                                isShowAllEmptyDialog = true
                            }
                        }
                    )
                    Separator()
                    userMenuList.forEachIndexed { index, userMenu ->
                        UserMenuInputArea(
                            userMenu = userMenu,
                            onChangeUserMenu = { value ->
                                viewModel.onChangeUserMenu(value, index)
                            },
                            onClickDelete = {
                                viewModel.onClickDeleteUserMenu(index)
                            }
                        )
                        Separator()
                    }
                    DietTextButton(
                        modifier = Modifier
//                            .padding(end = (10 + 36).dp)
                            .align(Alignment.End),
                        text = "메뉴 추가",
                        onClick = viewModel::onClickAddUserMenu
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            else -> {

            }
        }
    }

    if (isShowNotFinishDialog) {
        Dialog(
            title = "제가 모르는 음식이네요.",
            description = "다시 한 번만 입력해주시겠어요?\n" +
                    "(빈 칸은 상관없어요 \uD83D\uDE09)",
            confirmText = "확인",
            onClickConfirm = {
                isShowNotFinishDialog = false
            },
            onDismissRequest = {
                isShowNotFinishDialog = false
            }
        )
    }

    if (isShowAllEmptyDialog) {
        Dialog(
            title = "입력이 실패했어요.",
            description = "메뉴를 하나 이상 입력해주세요.",
            confirmText = "다시 하기",
            onClickConfirm = {
                isShowAllEmptyDialog = false
            },
            onDismissRequest = {
                isShowAllEmptyDialog = false
            }
        )
    }

    LaunchedEffect(viewModel.dietUiEvent) {
        viewModel.dietUiEvent.collectLatest { event ->
            when (event) {
                DietViewModel.DietUiEvent.OnSuccessInputMenu -> {
                    onFinish()
                }
                DietViewModel.DietUiEvent.OnFailureInputMenu -> {
                    isShowNotFinishDialog = true
                }
            }
        }

    }
}

@Composable
private fun UserMenuInputArea(
    modifier: Modifier = Modifier,
    userMenu: String,
    onChangeUserMenu: (String) -> Unit,
    onClickDelete: () -> Unit
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DietTextField(
            modifier = Modifier.width((screenWidthDp - (24 + 10 + 36 + 24)).dp),
            value = userMenu,
            onValueChange = onChangeUserMenu,
            placeholder = "오늘 먹었던 음식을 적어주세요"
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = Color.Red,
                    shape = AbsoluteRoundedCornerShape(5.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = AbsoluteRoundedCornerShape(5.dp)
                )
                .noRippleClickable {
                    onClickDelete()
                }
        ) {
            Image(
                painter = painterResource(R.drawable.ic_delete),
                modifier = Modifier
                    .padding(6.dp)
                    .size(30.dp),
                contentDescription = null
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
        Spacer(modifier = Modifier.height(10.dp))
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}