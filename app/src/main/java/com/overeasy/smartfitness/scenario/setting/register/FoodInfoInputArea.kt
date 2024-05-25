package com.overeasy.smartfitness.scenario.setting.register

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.overeasy.smartfitness.scenario.setting.public.SettingButton

@Composable
fun FoodInfoInputArea(
    modifier: Modifier = Modifier,
    foodInfoList: List<String>,
    onFinishSelectFood: () -> Unit
) {
    Column(
        modifier = modifier
    ) {

        SettingButton(
            text = "선택 완료",
            onClick = {

            }
        )
    }
}