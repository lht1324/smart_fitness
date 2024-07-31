package com.overeasy.smartfitness.scenario.public

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun OutlinedText(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    outlineColor: Color = Color.Black,
    fontSize: TextUnit = 24.dpToSp(),
    fontWeight: FontWeight = FontWeight.ExtraBold,
    outlineWidth: Dp = 2.dp
) {
    val density = LocalDensity.current

    var textLetterSpacingSp by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = 0.5.sp,
            onTextLayout = { layout ->
                if (layout.layoutInput.style.letterSpacing.value != textLetterSpacingSp)
                    textLetterSpacingSp = layout.layoutInput.style.letterSpacing.value
            }
        )

        Text(
            text = text,
            color = outlineColor,
            style = TextStyle.Default.copy(
                fontSize = fontSize,
                drawStyle = Stroke(
//                    width = density.run { outlineWidth.toPx() },
//                    miter = density.run { outlineWidth.toPx() * 5f },
                    width = 2f,
                    miter = 10f,
                    join = StrokeJoin.Round
                ),
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = textLetterSpacingSp.sp
            )
        )
    }
}