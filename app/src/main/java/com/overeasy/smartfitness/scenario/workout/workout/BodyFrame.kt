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
        bodyFrameData.offsetList.distinctBy { (x, _) ->
            x
        }.forEach { (x, _) ->
            drawCircle(
                color = Color.White,
                radius = 10.0f,
                center = x
            )
            drawCircle(
                color = Color.Red,
                radius = 8.0f,
                center = x
            )
        }
    }

    LaunchedEffect(bodyFrameData.offsetList) {
        offsetList.clear()
        offsetList.addAll(bodyFrameData.offsetList)
    }
}