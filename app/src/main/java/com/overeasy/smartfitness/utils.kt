package com.overeasy.smartfitness

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

fun println(tag: String?, msg: String) = Log.d(tag, msg)

@Composable
fun Dp.dpToSp() = LocalDensity.current.run { this@dpToSp.toSp() }

@Composable
fun Int.dpToSp() = LocalDensity.current.run { this@dpToSp.dp.toSp() }

@Composable
fun Float.dpToSp() = LocalDensity.current.run { this@dpToSp.dp.toSp() }

fun Context.pxToDp(px: Int): Float {
    var density = resources.displayMetrics.density

    if (density == 1f)
        density *= 4f
    else if (density == 1.5f)
        density *= (8f / 3f)
    else if (density == 2f)
        density *= 2f

    return px.toFloat() / density
}

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = composed {
    then(
        clickable(
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClickLabel = onClickLabel,
            role = role,
            onClick = onClick
        )
    )
}

fun addCommaIntoNumber(number: Int): String = DecimalFormat("#,###").format(number)

fun showToast(
    context: Context,
    msg: String,
    duration: Int = Toast.LENGTH_SHORT
): Toast = Toast.makeText(context, msg, duration)

fun isLettersOrDigits(chars: String): Boolean {
    return chars.none { char ->
        char !in 'A'..'Z' &&
            char !in 'a'..'z' &&
            char !in '0'..'9'  }
}

fun isLettersOrDigitsIncludeKorean(chars: String): Boolean {
    return chars.none { char ->
        char !in 'A'..'Z' &&
                char !in 'a'..'z' &&
                char !in '0'..'9' &&
                char !in 'ㄱ'..'ㅎ' &&
                char !in '가'..'힣'
    }
}
