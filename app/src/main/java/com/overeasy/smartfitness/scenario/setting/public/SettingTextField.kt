package com.overeasy.smartfitness.scenario.setting.public

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun SettingTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isInvalid: Boolean = false,
    isMaskedTextField: Boolean = false
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
                    Color.White
                },
                shape = AbsoluteRoundedCornerShape(20.dp)
            )
            .background(
                color = Color.White,
                shape = AbsoluteRoundedCornerShape(20.dp)
            ),
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 20.dpToSp(),
            fontWeight = FontWeight.Medium,
            fontFamily = fontFamily
        ),
        visualTransformation = if (isMaskedTextField) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        decorationBox = { innerTextField: @Composable () -> Unit ->
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                when (value.isNotEmpty()) {
                    true -> {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 20.dp)
                                .wrapContentSize()
                        ) {
                            innerTextField()
                        }
                    }

                    false -> {
                        Text(
                            text = placeholder,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                            color = Color.Gray,
                            fontSize = 20.dpToSp(),
                            fontWeight = FontWeight.Medium,
                            fontFamily = fontFamily
                        )

                        Box(modifier = Modifier.size(0.dp)) {
                            innerTextField()
                        }
                    }
                }
            }
        },
    )
}

private class PasswordVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            AnnotatedString("*".repeat(text.text.length)),
            OffsetMapping.Identity
        )
    }
}