package com.overeasy.smartfitness.scenario.workout.workout

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.overeasy.smartfitness.model.workout.BodyFrameData

@Composable
fun BodyFrame(
    modifier: Modifier = Modifier,
    bodyFrameData: BodyFrameData,
    color: Color = Color.White,
    strokeWidth: Float = 10f
) {
    val offsetList = remember { mutableStateListOf<Pair<Offset, Offset>>() }

    Canvas(
        modifier = modifier
    ) {
        bodyFrameData.offsetList.forEach { (start, end) ->
            drawLine(
                color = color,
                start = start,
                end = end,
                strokeWidth = strokeWidth
            )
        }
    }

    LaunchedEffect(bodyFrameData.offsetList) {
        offsetList.clear()
        offsetList.addAll(bodyFrameData.offsetList)
    }
}