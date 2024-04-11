package com.overeasy.smartfitness.scenario.setting.withdraw

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.ui.theme.ColorPrimary

@Composable
fun WithdrawScreen(
    modifier: Modifier = Modifier,
    viewModel: WithdrawViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {

    }
}