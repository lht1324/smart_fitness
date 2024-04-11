package com.overeasy.smartfitness.scenario.setting.public

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun SettingTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .background(
                color = Color.White,
                shape = AbsoluteRoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = ColorSecondary,
                shape = AbsoluteRoundedCornerShape(10.dp)
            ),
        placeholder = {
            Text(
                text = placeholder,
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                color = Color.Gray,
                fontWeight = FontWeight.Normal,
                fontFamily = fontFamily
            )
        },
        shape = AbsoluteRoundedCornerShape(10.dp)
    )
}