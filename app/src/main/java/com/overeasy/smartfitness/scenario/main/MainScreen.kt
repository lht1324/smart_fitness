package com.overeasy.smartfitness.scenario.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.scenario.public.Header
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Header(
            title = "촬영"
        )
        Text(
            text = "Main",
            fontSize = LocalDensity.current.run { 18.dp.toSp() },
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold
        )
    }
}