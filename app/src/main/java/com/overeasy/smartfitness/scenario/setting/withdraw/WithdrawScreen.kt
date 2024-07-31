package com.overeasy.smartfitness.scenario.setting.withdraw

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.setting.public.SettingButton
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.fontFamily
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WithdrawScreen(
    modifier: Modifier = Modifier,
    viewModel: WithdrawViewModel = hiltViewModel(),
    onFinishWithdraw: () -> Unit
) {
    var isShowRecheckDialog by remember { mutableStateOf(false) }
    var isShowErrorDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "탈퇴하시겠어요?",
                color = Color.White,
                fontSize = 20.dpToSp(),
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "기존까지의 운동 기록, 랭킹이 전부 삭제됩니다.",
                color = Color.Red,
                fontSize = 18.dpToSp(),
                fontWeight = FontWeight.Medium,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(20.dp))
            SettingButton(
                modifier = Modifier.align(Alignment.End),
                text = "탈퇴",
                onClick = {
                    isShowRecheckDialog = true
                }
            )
        }
    }

    if (isShowRecheckDialog) {
        Dialog(
            title = "삭제된 기록은 다시 복구할 수 없어요.",
            description = "정말 탈퇴하시겠어요?",
            confirmText = "취소",
            dismissText = "탈퇴하기",
            onClickConfirm = {
                isShowRecheckDialog = false
            },
            onClickDismiss = {
                viewModel.onClickWithdraw()
            },
            onDismissRequest = {
                isShowRecheckDialog = false
            }
        )
    }

    if (isShowErrorDialog) {
        Dialog(
            title = "탈퇴 처리 중 에러가 발생했어요.",
            description = "다시 시도해주세요.",
            confirmText = "다시 시도하기",
            onClickConfirm = {
                isShowErrorDialog = false
            },
            onDismissRequest = {
                isShowErrorDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.withdrawUiEvent.collectLatest { event ->
            when (event) {
                WithdrawViewModel.WithdrawUiEvent.OnSuccessWithdraw -> {
                    onFinishWithdraw()
                }
                WithdrawViewModel.WithdrawUiEvent.OnFailureWithdraw -> {
                    isShowRecheckDialog = false
                    isShowErrorDialog = true
                }
            }
        }
    }
}