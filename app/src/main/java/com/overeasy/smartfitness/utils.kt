package com.overeasy.smartfitness

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.activity.ComponentActivity
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
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.overeasy.smartfitness.domain.ai.entity.PostAiReq
import com.overeasy.smartfitness.domain.ai.model.LandmarkInfo
import com.overeasy.smartfitness.domain.workout.model.workout.LandmarkCoordinate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.atan2

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

fun Pose.toLandmarkInfo(workoutName: String): LandmarkInfo? = run {
    // 모든 랜드마크 포함
    // x, y null로 변경 가능
    if (allPoseLandmarks.size == PoseLandmark.RIGHT_FOOT_INDEX + 1) {
        LandmarkInfo(
            workoutName = workoutName,

            leftShoulder = getPoseLandmark(PoseLandmark.LEFT_SHOULDER)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            rightShoulder = getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            leftElbow = getPoseLandmark(PoseLandmark.LEFT_ELBOW)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            rightElbow = getPoseLandmark(PoseLandmark.RIGHT_ELBOW)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            leftWrist = getPoseLandmark(PoseLandmark.LEFT_WRIST)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            rightWrist = getPoseLandmark(PoseLandmark.RIGHT_WRIST)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            leftHip = getPoseLandmark(PoseLandmark.LEFT_HIP)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            rightHip = getPoseLandmark(PoseLandmark.RIGHT_HIP)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            leftKnee = getPoseLandmark(PoseLandmark.LEFT_KNEE)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            rightKnee = getPoseLandmark(PoseLandmark.RIGHT_KNEE)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            leftAnkle = getPoseLandmark(PoseLandmark.LEFT_ANKLE)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            rightAnkle = getPoseLandmark(PoseLandmark.RIGHT_ANKLE)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            leftPinky = getPoseLandmark(PoseLandmark.LEFT_PINKY)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            rightPinky = getPoseLandmark(PoseLandmark.RIGHT_PINKY)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            leftIndex = getPoseLandmark(PoseLandmark.LEFT_INDEX)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            rightIndex = getPoseLandmark(PoseLandmark.RIGHT_INDEX)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            leftThumb = getPoseLandmark(PoseLandmark.LEFT_THUMB)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            rightThumb = getPoseLandmark(PoseLandmark.RIGHT_THUMB)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            leftHeel = getPoseLandmark(PoseLandmark.LEFT_HEEL)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            rightHeel = getPoseLandmark(PoseLandmark.RIGHT_HEEL)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            leftFootIndex = getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            },
            rightFootIndex = getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)!!.position.run {
                LandmarkCoordinate(
                    x = 480 - x,
                    y = y
                ).matchResolution()
            }
        )
    } else {
        null
    }
}

private fun LandmarkCoordinate.matchResolution(): LandmarkCoordinate {
    /**
     * 480 x 640
     * to
     * 1920 x 1080
     * 1. 640 / 1080 = ratio
     * 2. 1920 * ratio = smallWidth
     * 3. (smallWidth - 480) / 2f = leftSide
     * 4. bigRatio = 1080 / 640
     * (x + leftSide) * bigRatio
     * y * bigRatio
     */
//    val ratio = 640f / 1080f
//    val smallWidth = 1920f * ratio
//    val smallWidthLeftSide = (smallWidth - 480f) / 2f
//    val bigRatio = 1080f / 640f
//
//    return copy(
//        x = (x + smallWidthLeftSide) * bigRatio,
//        y = y * bigRatio
//    )
    val leftAreaWidth = (1920f - 480f) / 2f
    val topAreaHeight = (1080f - 640f) / 2f

    return copy(
        x = x + leftAreaWidth,
        y = y + topAreaHeight
    )
}

fun <T> Flow<T>.history(): Flow<Pair<T, T>> = run {
    map { highestPoint ->
        highestPoint to null
    }.scan(null as T? to null as T?) { prev, next ->
        prev.second to next.first
    }.filter { (prev, next) ->
        prev != null && next != null
    }.map { (prev, next) ->
        prev!! to next!!
    }
}

fun LandmarkCoordinate.toPair() = run { x to y }

fun calculateAngle(pointA: Pair<Float, Float>, pointB: Pair<Float, Float>, pointC: Pair<Float, Float>): Float {
    val (aX, aY) = pointA
    val (bX, bY) = pointB
    val (cX, cY) = pointC

    val angle = atan2(cY - bY, cX - bX) - atan2(aY - bY, aX - bX)

    return abs(Math.toDegrees(angle.toDouble()).toFloat())
}

fun Context.getActivity(): ComponentActivity? = when(this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}