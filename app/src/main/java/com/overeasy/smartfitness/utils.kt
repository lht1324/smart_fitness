package com.overeasy.smartfitness

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

fun println(tag: String?, msg: String) = Log.d(tag, msg)

@Composable
fun Dp.dpToSp() = LocalDensity.current.run { this@dpToSp.toSp() }

@Composable
fun Int.dpToSp() = LocalDensity.current.run { this@dpToSp.toSp() }

@Composable
fun Float.dpToSp() = LocalDensity.current.run { this@dpToSp.toSp() }

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