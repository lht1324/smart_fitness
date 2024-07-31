package com.overeasy.smartfitness.scenario.setting.public

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun InfoInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    metric: String = "", // 단위
    isInvalid: Boolean,
    invalidText: String = "",
    isMaskedTextField: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingTextField(
                modifier = Modifier.weight(
                    if (metric.isNotEmpty())
                        0.9f
                    else
                        1f
                ),
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                isInvalid = isInvalid,
                isMaskedTextField = isMaskedTextField,
                keyboardOptions = keyboardOptions
            )
            if (metric.isNotEmpty()) {
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = metric,
                    modifier = Modifier.weight(0.1f),
                    color = Color.White,
                    fontSize = 18.dpToSp(),
                    fontWeight = FontWeight.Medium,
                    fontFamily = fontFamily
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = invalidText,
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.Start),
            color = if (isInvalid) {
                Color.Red
            } else {
                Color.Transparent
            },
            fontSize = 12.dpToSp(),
            fontWeight = FontWeight.Medium,
            fontFamily = fontFamily,
            textAlign = TextAlign.Start,
            lineHeight = 1.dpToSp()
        )
        Spacer(modifier = Modifier.height(5.dp))
    }
}