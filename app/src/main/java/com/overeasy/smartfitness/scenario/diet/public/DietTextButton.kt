package com.overeasy.smartfitness.scenario.diet.public

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun DietTextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = ColorSecondary,
                shape = AbsoluteRoundedCornerShape(5.dp)
            )
            .noRippleClickable(onClick = onClick)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
            color = Color.White,
            fontSize = 18.dpToSp(),
            fontWeight = FontWeight.Medium,
            fontFamily = fontFamily
        )
    }
}