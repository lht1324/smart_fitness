package com.overeasy.smartfitness.scenario.workout.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun SetDataItemTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    unitText: String = ""
) {
    val onlyNumberRegex = remember { Regex("^\\d+\$") }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = { value ->
                if (value.isEmpty() || value.matches(onlyNumberRegex)) {
                    if (value.length <= 3) {
                        onValueChange(value)
                    } else {
                        onValueChange("999")
                    }
                }
            },
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = AbsoluteRoundedCornerShape(5.dp)
                )
                .background(
                    color = Color.White,
                    shape = AbsoluteRoundedCornerShape(5.dp)
                ),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.dpToSp(),
                fontWeight = FontWeight.Medium,
                fontFamily = fontFamily
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            decorationBox = { innerTextField: @Composable () -> Unit ->
                Box {
                    when (value.isNotEmpty()) {
                        true -> {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 5.dp, horizontal = 7.dp)
                                    .width(40.dp),
                            ) {
                                innerTextField()
                            }
                        }
                        false -> {
                            Text(
                                text = placeholder,
                                modifier = Modifier
                                    .padding(vertical = 5.dp, horizontal = 7.dp)
                                    .width(40.dp),
                                color = Color.Gray,
                                fontSize = 16.dpToSp(),
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
        if (unitText.isNotEmpty()) {
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = unitText,
                color = Color.LightGray,
                fontSize = 16.dpToSp(),
                fontWeight = FontWeight.Medium,
                fontFamily = fontFamily
            )
        }
    }
}