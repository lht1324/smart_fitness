package com.overeasy.smartfitness.scenario.setting.logout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.overeasy.smartfitness.scenario.setting.public.SettingButton
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.fontFamily
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LogoutScreen(
    modifier: Modifier = Modifier,
    viewModel: LogoutViewModel = hiltViewModel(),
    onFinishLogout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "로그아웃 하시겠어요?",
                color = Color.White,
                fontSize = 18.dpToSp(),
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.weight(1f))
            SettingButton(
                text = "로그아웃",
                onClick = viewModel::onClickLogout
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.logoutUiEvent.collectLatest { event ->
            when (event) {
                LogoutViewModel.LogoutUiEvent.OnSuccessLogout -> {
                    onFinishLogout()
                }
                LogoutViewModel.LogoutUiEvent.OnFailureLogout -> {

                }
            }
        }
    }
}