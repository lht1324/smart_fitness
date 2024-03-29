package com.overeasy.smartfitness.scenario.public

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.R
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun Header(
    modifier: Modifier = Modifier,
    title: String = "",
    endButton: (@Composable () -> Unit)? = null,
    onClickBack: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_back_button),
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.CenterStart)
                .clickable {
                    onClickBack()
                },
            contentDescription = "뒤로 가기"
        )
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 48.dpToSp(),
            fontFamily = fontFamily,
            fontWeight = FontWeight.Black
        )
        if (endButton != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                endButton()
            }
        }
    }
}