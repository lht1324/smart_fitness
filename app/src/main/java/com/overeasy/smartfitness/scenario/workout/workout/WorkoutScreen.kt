package com.overeasy.smartfitness.scenario.workout.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.module.videomanager.VideoManager
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        VideoManager.CameraX()
        Box(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .size(100.dp)
                .background(
                    color = Color.White,
                    shape = CircleShape
                ).align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .border(
                        width = 2.dp,
                        color = Color.Red,
                        shape = CircleShape
                    ).align(Alignment.Center)
            ) {
                Text(
                    text = "시작 \uD83D\uDD25",
                    modifier = Modifier.align(Alignment.Center),
                    color = ColorSecondary,
                    fontSize = 18.dpToSp(),
                    fontWeight = FontWeight.Medium,
                    fontFamily = fontFamily
                )
            }
        }
    }
}