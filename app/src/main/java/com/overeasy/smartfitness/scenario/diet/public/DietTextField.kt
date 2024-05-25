package com.overeasy.smartfitness.scenario.diet.public

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun DietTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isInvalid: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .border(
                width = 2.dp,
                color = if (isInvalid) {
                    Color.Red
                } else {
                    Color.LightGray
                },
                shape = AbsoluteRoundedCornerShape(5.dp)
            )
            .background(
                color = Color.White,
                shape = AbsoluteRoundedCornerShape(5.dp)
            ),
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 16.dpToSp(),
            fontWeight = FontWeight.Normal,
            fontFamily = fontFamily
        ),
        keyboardOptions = keyboardOptions,
        decorationBox = { innerTextField: @Composable () -> Unit ->
            Box(
                modifier = Modifier //.fillMaxWidth()
            ) {
                Text(
                    text = if (value.isNotEmpty()) {
                        ""
                    } else {
                        placeholder
                    },
                    modifier = Modifier
                        .padding(vertical = 15.dp, horizontal = 20.dp)
                        .align(Alignment.CenterStart),
                    color = Color.Gray,
                    fontSize = 16.dpToSp(),
                    fontWeight = FontWeight.Normal,
                    fontFamily = fontFamily
                )
                Box(
                    modifier = Modifier
                        .padding(vertical = 15.dp, horizontal = 20.dp)
                        .align(Alignment.CenterStart),
                ) {
                    innerTextField()
                }
            }
        },
    )
}