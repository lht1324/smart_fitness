package com.overeasy.smartfitness.scenario.workout.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overeasy.smartfitness.R
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.module.videomanager.VideoManager
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.ColorSunday
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun WorkoutScreen(
    modifier: Modifier = Modifier,
    viewModel: WorkoutViewModel = hiltViewModel(),
    onClick: () -> Unit
) {
    var isShowDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
//        VideoManager.CameraX()
        Image(
            painter = painterResource(id = R.drawable.workout_guy),
            modifier = Modifier.fillMaxSize(),
            contentDescription = null
        )
//        Column(
//            modifier = Modifier
//                .padding(top = 20.dp, end = 20.dp)
//                .wrapContentWidth()
//                .background(color = Color.White.copy(alpha = 0.3f))
//                .align(Alignment.TopEnd)
//        ) {
//            Column(
//                modifier = Modifier
//                    .padding(20.dp)
//            ) {
//                Text(
//                    text = "Cool: 11",
//                    color = ColorSaturday,
//                    fontSize = 24.dpToSp(),
//                    fontWeight = FontWeight.Black,
//                    fontFamily = fontFamily
//                )
//                Spacer(modifier = Modifier.height(20.dp))
//                Text(
//                    text = "Good: 6",
//                    color = Color(0xFF2DFE54),
//                    fontSize = 24.dpToSp(),
//                    fontWeight = FontWeight.Black,
//                    fontFamily = fontFamily
//                )
//                Spacer(modifier = Modifier.height(20.dp))
//                Text(
//                    text = "Not Good: 3",
//                    color = ColorSunday,
//                    fontSize = 24.dpToSp(),
//                    fontWeight = FontWeight.Black,
//                    fontFamily = fontFamily
//                )
//            }
//        }
        Box(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .size(100.dp)
                .noRippleClickable {
//                    isShowDialog = true
                    onClick()
                }
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .border(
                        width = 2.dp,
                        color = Color.Red,
                        shape = CircleShape
                    )
                    .align(Alignment.Center)
            ) {
                Text(
//                    text = "시작 \uD83D\uDD25",
                    text = "종료",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black,
                    fontSize = 18.dpToSp(),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = fontFamily
                )
            }
        }
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(color = Color.Black.copy(alpha = 0.3f))
//        ) {
//            Column(
//                modifier = Modifier.align(Alignment.Center),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(100.dp),
//                    color = ColorSaturday,
//                    strokeWidth = 15.dp
//                )
//                Spacer(modifier = Modifier.height(10.dp))
//                Text(
//                    text = "AI가 운동 결과를 열심히 분석 중이에요.\n잠시만 기다려주세요...",
//                    color = Color.White,
//                    fontSize = 24.dpToSp(),
//                    fontWeight = FontWeight.ExtraBold,
//                    fontFamily = fontFamily,
//                    textAlign = TextAlign.Center
//                )
//            }
//        }
    }

    if (isShowDialog) {
        AlertDialog(
            onDismissRequest = {
                isShowDialog = false
            },
            confirmButton = {
                Text(
                    text = "취소",
                    fontSize = 16.dpToSp(),
                    fontWeight = FontWeight.Light,
                    fontFamily = fontFamily
                )
            },
            dismissButton = {
                Text(
                    text = "허용하기",
                    fontSize = 16.dpToSp(),
                    fontWeight = FontWeight.Light,
                    fontFamily = fontFamily
                )
            },
            title = {
//                Text(
//                    text = "끄려고요?",
//                    fontSize = 20.dpToSp(),
//                    fontWeight = FontWeight.Light,
//                    fontFamily = fontFamily
//                )
                Text(
                    text = "카메라 권한을 허용해야 운동을 분석할 수 있어요.",
                    fontSize = 20.dpToSp(),
                    fontWeight = FontWeight.Light,
                    fontFamily = fontFamily
                )
            },
            text = {
//                Text(
//                    text = "운동은 하고 가는 거죠?",
//                    fontSize = 18.dpToSp(),
//                    fontWeight = FontWeight.Light,
//                    fontFamily = fontFamily
//                )
            },

            )
    }
}