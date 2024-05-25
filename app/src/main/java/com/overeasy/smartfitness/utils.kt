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
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

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

fun getDateString() = LocalDateTime.now().run {
    val getFormattedInt: (Int) -> String = { value ->
        String.format("%02d", value)
    }

    "${getFormattedInt(year)}-${getFormattedInt(monthValue)}-${getFormattedInt(dayOfMonth)}"
}

fun LandmarkCoordinate.toPair() = run { x to y }

fun calculateAngle(pointA: Pair<Float, Float>, pointB: Pair<Float, Float>, pointC: Pair<Float, Float>): Float {
    val (aX, aY) = pointA
    val (bX, bY) = pointB
    val (cX, cY) = pointC

    val angle = atan2(cY - bY, cX - bX) - atan2(aY - bY, aX - bX)

    return abs(Math.toDegrees(angle.toDouble()).toFloat())
}

fun getNormalizedFrameFloatArray(frameDataList: List<LandmarkInfo>): FloatArray {
    /**
     * 각 값에서 평균을 뺀다.
     * 그 차이를 제곱한다.
     * 제곱한 값들의 평균을 구한다.
     * 그 평균의 제곱근을 구한다.
     */
    val frameDataListSizeDouble = frameDataList.size.toDouble()

    val leftShoulderAverageX = frameDataList.sumOf { it.leftShoulder.x.toDouble() } / frameDataListSizeDouble
    val leftShoulderAverageY = frameDataList.sumOf { it.leftShoulder.y.toDouble() } / frameDataListSizeDouble
    val rightShoulderAverageX = frameDataList.sumOf { it.rightShoulder.x.toDouble() } / frameDataListSizeDouble
    val rightShoulderAverageY = frameDataList.sumOf { it.rightShoulder.y.toDouble() } / frameDataListSizeDouble
    val leftElbowAverageX = frameDataList.sumOf { it.leftElbow.x.toDouble() } / frameDataListSizeDouble
    val leftElbowAverageY = frameDataList.sumOf { it.leftElbow.y.toDouble() } / frameDataListSizeDouble
    val rightElbowAverageX = frameDataList.sumOf { it.rightElbow.x.toDouble() } / frameDataListSizeDouble
    val rightElbowAverageY = frameDataList.sumOf { it.rightElbow.y.toDouble() } / frameDataListSizeDouble
    val leftWristAverageX = frameDataList.sumOf { it.leftWrist.x.toDouble() } / frameDataListSizeDouble
    val leftWristAverageY = frameDataList.sumOf { it.leftWrist.y.toDouble() } / frameDataListSizeDouble
    val rightWristAverageX = frameDataList.sumOf { it.rightWrist.x.toDouble() } / frameDataListSizeDouble
    val rightWristAverageY = frameDataList.sumOf { it.rightWrist.y.toDouble() } / frameDataListSizeDouble
    val leftHipAverageX = frameDataList.sumOf { it.leftHip.x.toDouble() } / frameDataListSizeDouble
    val leftHipAverageY = frameDataList.sumOf { it.leftHip.y.toDouble() } / frameDataListSizeDouble
    val rightHipAverageX = frameDataList.sumOf { it.rightHip.x.toDouble() } / frameDataListSizeDouble
    val rightHipAverageY = frameDataList.sumOf { it.rightHip.y.toDouble()} / frameDataListSizeDouble
    val leftKneeAverageX = frameDataList.sumOf { it.leftKnee.x.toDouble() } / frameDataListSizeDouble
    val leftKneeAverageY = frameDataList.sumOf { it.leftKnee.y.toDouble() } / frameDataListSizeDouble
    val rightKneeAverageX = frameDataList.sumOf { it.rightKnee.x.toDouble() } / frameDataListSizeDouble
    val rightKneeAverageY = frameDataList.sumOf { it.rightKnee.y.toDouble() } / frameDataListSizeDouble
    val leftAnkleAverageX = frameDataList.sumOf { it.leftAnkle.x.toDouble() } / frameDataListSizeDouble
    val leftAnkleAverageY = frameDataList.sumOf { it.leftAnkle.y.toDouble() } / frameDataListSizeDouble
    val rightAnkleAverageX = frameDataList.sumOf {it.rightAnkle.x.toDouble() } / frameDataListSizeDouble
    val rightAnkleAverageY = frameDataList.sumOf {it.rightAnkle.y.toDouble() } / frameDataListSizeDouble

    val leftShoulderStandardDeviationX = sqrt(frameDataList.sumOf { (it.leftShoulder.x.toDouble() - leftShoulderAverageX).pow(2) } / frameDataListSizeDouble)
    val leftShoulderStandardDeviationY = sqrt(frameDataList.sumOf { (it.leftShoulder.y.toDouble() - leftShoulderAverageY).pow(2) } / frameDataListSizeDouble)
    val rightShoulderStandardDeviationX = sqrt(frameDataList.sumOf { (it.rightShoulder.x.toDouble() - rightShoulderAverageX).pow(2) } / frameDataListSizeDouble)
    val rightShoulderStandardDeviationY = sqrt(frameDataList.sumOf { (it.rightShoulder.y.toDouble() - rightShoulderAverageY).pow(2) } / frameDataListSizeDouble)
    val leftElbowStandardDeviationX = sqrt(frameDataList.sumOf { (it.leftElbow.x.toDouble() - leftElbowAverageX).pow(2) } / frameDataListSizeDouble)
    val leftElbowStandardDeviationY = sqrt(frameDataList.sumOf { (it.leftElbow.y.toDouble() - leftElbowAverageY).pow(2) } / frameDataListSizeDouble)
    val rightElbowStandardDeviationX = sqrt(frameDataList.sumOf { (it.rightElbow.x.toDouble() - rightElbowAverageX).pow(2) } / frameDataListSizeDouble)
    val rightElbowStandardDeviationY = sqrt(frameDataList.sumOf { (it.rightElbow.y.toDouble() - rightElbowAverageY).pow(2) } / frameDataListSizeDouble)
    val leftWristStandardDeviationX = sqrt(frameDataList.sumOf { (it.leftWrist.x.toDouble() - leftWristAverageX).pow(2) } / frameDataListSizeDouble)
    val leftWristStandardDeviationY = sqrt(frameDataList.sumOf { (it.leftWrist.y.toDouble() - leftWristAverageY).pow(2) } / frameDataListSizeDouble)
    val rightWristStandardDeviationX = sqrt(frameDataList.sumOf { (it.rightWrist.x.toDouble() - rightWristAverageX).pow(2) } / frameDataListSizeDouble)
    val rightWristStandardDeviationY = sqrt(frameDataList.sumOf { (it.rightWrist.y.toDouble() - rightWristAverageY).pow(2) } / frameDataListSizeDouble)
    val leftHipStandardDeviationX = sqrt(frameDataList.sumOf { (it.leftHip.x.toDouble() - leftHipAverageX).pow(2) } / frameDataListSizeDouble)
    val leftHipStandardDeviationY = sqrt(frameDataList.sumOf { (it.leftHip.y.toDouble() - leftHipAverageY).pow(2) } / frameDataListSizeDouble)
    val rightHipStandardDeviationX = sqrt(frameDataList.sumOf { (it.rightHip.x.toDouble() - rightHipAverageX).pow(2) } / frameDataListSizeDouble)
    val rightHipStandardDeviationY = sqrt(frameDataList.sumOf { (it.rightHip.y.toDouble() - rightHipAverageY).pow(2)} / frameDataListSizeDouble)
    val leftKneeStandardDeviationX = sqrt(frameDataList.sumOf { (it.leftKnee.x.toDouble() - leftKneeAverageX).pow(2) } / frameDataListSizeDouble)
    val leftKneeStandardDeviationY = sqrt(frameDataList.sumOf { (it.leftKnee.y.toDouble() - leftKneeAverageY).pow(2) } / frameDataListSizeDouble)
    val rightKneeStandardDeviationX = sqrt(frameDataList.sumOf { (it.rightKnee.x.toDouble() - rightKneeAverageX).pow(2) } / frameDataListSizeDouble)
    val rightKneeStandardDeviationY = sqrt(frameDataList.sumOf { (it.rightKnee.y.toDouble() - rightKneeAverageY).pow(2) } / frameDataListSizeDouble)
    val leftAnkleStandardDeviationX = sqrt(frameDataList.sumOf { (it.leftAnkle.x.toDouble() - leftAnkleAverageX).pow(2) } / frameDataListSizeDouble)
    val leftAnkleStandardDeviationY = sqrt(frameDataList.sumOf { (it.leftAnkle.y.toDouble() - leftAnkleAverageY).pow(2) } / frameDataListSizeDouble)
    val rightAnkleStandardDeviationX = sqrt(frameDataList.sumOf { (it.rightAnkle.x.toDouble() - rightAnkleAverageX).pow(2) } / frameDataListSizeDouble)
    val rightAnkleStandardDeviationY = sqrt(frameDataList.sumOf { (it.rightAnkle.y.toDouble() - rightAnkleAverageY).pow(2) } / frameDataListSizeDouble)

    // root((leftSX - leftSXA)^2 / frameDataList.size)
    /**
     * 각 값에서 평균을 뺀다.
     * 그 차이를 제곱한다.
     * 제곱한 값들의 평균을 구한다.
     * 그 평균의 제곱근을 구한다.
     */

    val inputData = FloatArray(
        frameDataList.size * 12 * 2 * 5
    )

    repeat(5) { count ->
        frameDataList.forEachIndexed { index, landmarkInfo ->
            val expandedIndex = (index + 1) * 24
            val repeatExpandCount = (frameDataList.size * 24 * count)

            // root((leftSX - leftSXA)^2 / frameDataList.size)
            landmarkInfo.run {
                inputData[expandedIndex - 24 + repeatExpandCount] = (leftShoulder.x - leftShoulderAverageX.toFloat()) / leftShoulderStandardDeviationX.toFloat()
                inputData[expandedIndex - 23 + repeatExpandCount] = (leftShoulder.y - leftShoulderAverageY.toFloat()) / leftShoulderStandardDeviationY.toFloat()
                inputData[expandedIndex - 22 + repeatExpandCount] = (rightShoulder.x - rightShoulderAverageX.toFloat()) / rightShoulderStandardDeviationX.toFloat()
                inputData[expandedIndex - 21 + repeatExpandCount] = (rightShoulder.y - rightShoulderAverageY.toFloat()) / rightShoulderStandardDeviationY.toFloat()
                inputData[expandedIndex - 20 + repeatExpandCount] = (leftElbow.x - leftElbowAverageX.toFloat()) / leftElbowStandardDeviationX.toFloat()
                inputData[expandedIndex - 19 + repeatExpandCount] = (leftElbow.y - leftElbowAverageY.toFloat()) / leftElbowStandardDeviationY.toFloat()
                inputData[expandedIndex - 18 + repeatExpandCount] = (rightElbow.x - rightElbowAverageX.toFloat()) / rightElbowStandardDeviationX.toFloat()
                inputData[expandedIndex - 17 + repeatExpandCount] = (rightElbow.y - rightElbowAverageY.toFloat()) / rightElbowStandardDeviationY.toFloat()
                inputData[expandedIndex - 16 + repeatExpandCount] = (leftWrist.x - leftWristAverageX.toFloat()) / leftWristStandardDeviationX.toFloat()
                inputData[expandedIndex - 15 + repeatExpandCount] = (leftWrist.y - leftWristAverageY.toFloat()) / leftWristStandardDeviationY.toFloat()
                inputData[expandedIndex - 14 + repeatExpandCount] = (rightWrist.x - rightWristAverageX.toFloat()) / rightWristStandardDeviationX.toFloat()
                inputData[expandedIndex - 13 + repeatExpandCount] = (rightWrist.y - rightWristAverageY.toFloat()) / rightWristStandardDeviationY.toFloat()
                inputData[expandedIndex - 12 + repeatExpandCount] = (leftHip.x - leftHipAverageX.toFloat()) / leftHipStandardDeviationX.toFloat()
                inputData[expandedIndex - 11 + repeatExpandCount] = (leftHip.y - leftHipAverageY.toFloat()) / leftHipStandardDeviationY.toFloat()
                inputData[expandedIndex - 10 + repeatExpandCount] = (rightHip.x - rightHipAverageX.toFloat()) / rightHipStandardDeviationX.toFloat()
                inputData[expandedIndex - 9 + repeatExpandCount] = (rightHip.y - rightHipAverageY.toFloat()) / rightHipStandardDeviationY.toFloat()
                inputData[expandedIndex - 8 + repeatExpandCount] = (leftKnee.x - leftKneeAverageX.toFloat()) / leftKneeStandardDeviationX.toFloat()
                inputData[expandedIndex - 7 + repeatExpandCount] = (leftKnee.y - leftKneeAverageY.toFloat()) / leftKneeStandardDeviationY.toFloat()
                inputData[expandedIndex - 6 + repeatExpandCount] = (rightKnee.x - rightKneeAverageX.toFloat()) / rightKneeStandardDeviationX.toFloat()
                inputData[expandedIndex - 5 + repeatExpandCount] = (rightKnee.y - rightKneeAverageY.toFloat()) / rightKneeStandardDeviationY.toFloat()
                inputData[expandedIndex - 4 + repeatExpandCount] = (leftAnkle.x - leftAnkleAverageX.toFloat()) / leftAnkleStandardDeviationX.toFloat()
                inputData[expandedIndex - 3 + repeatExpandCount] = (leftAnkle.y - leftAnkleAverageY.toFloat()) / leftAnkleStandardDeviationY.toFloat()
                inputData[expandedIndex - 2 + repeatExpandCount] = (rightAnkle.x - rightAnkleAverageX.toFloat()) / rightAnkleStandardDeviationX.toFloat()
                inputData[expandedIndex - 1 + repeatExpandCount] = (rightAnkle.y - rightAnkleAverageY.toFloat()) / rightAnkleStandardDeviationY.toFloat()
            }
        }
    }

    return inputData
}