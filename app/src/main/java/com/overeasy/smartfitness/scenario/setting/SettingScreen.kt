package com.overeasy.smartfitness.scenario.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.scenario.public.Header
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Header(
            title = "설정"
        )
        Text(
            text = "Setting",
            fontSize = LocalDensity.current.run { 18.dp.toSp() },
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold
        )
    }
}