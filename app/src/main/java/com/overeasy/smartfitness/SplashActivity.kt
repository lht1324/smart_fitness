package com.overeasy.smartfitness

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.overeasy.smartfitness.scenario.public.OutlinedText
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.concurrent.timer

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var currentAlpha by remember { mutableFloatStateOf(0.0f) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = ColorPrimary)
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.Center),
                    verticalAlignment = Alignment.Bottom
                ) {
                    OutlinedText(
                        text = "tr",
                        textColor = Color.White.copy(alpha = currentAlpha),
//                        outlineColor = Color.Black.copy(alpha = currentAlpha),
                        outlineColor = Color.Black.copy(alpha = currentAlpha),
                        fontSize = 72.dpToSp(),
                        fontWeight = FontWeight.Black
                    )
                    OutlinedText(
                        text = "AI",
                        textColor = ColorSecondary,
                        outlineColor = Color.Black,
                        fontSize = 72.dpToSp(),
                        fontWeight = FontWeight.Black
                    )
                    OutlinedText(
                        text = "ner",
                        textColor = Color.White.copy(alpha = currentAlpha),
//                        outlineColor = Color.Black.copy(alpha = currentAlpha),
                        outlineColor = Color.Black.copy(alpha = currentAlpha),
                        fontSize = 72.dpToSp(),
                        fontWeight = FontWeight.Black
                    )
                }
            }

            BackHandler {

            }

            LaunchedEffect(Unit) {
                timer(period = 10L) {
                    if ((currentAlpha + 10L.toFloat() / 3000L.toFloat()) <= 1.0f)
                        currentAlpha += 10L.toFloat() / 3000L.toFloat()
                }
                delay(4L * 1000L)
                startActivity(
                    Intent(this@SplashActivity, MainActivity::class.java)
                )
                finish()
            }
        }
    }
}