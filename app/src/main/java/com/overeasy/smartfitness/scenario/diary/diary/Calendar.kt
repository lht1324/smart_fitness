package com.overeasy.smartfitness.scenario.diary.diary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.model.diary.CalendarItemData
import com.overeasy.smartfitness.model.diary.WeekDay
import com.overeasy.smartfitness.noRippleClickable
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.ui.theme.ColorSaturday
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.ColorSunday
import com.overeasy.smartfitness.ui.theme.fontFamily

private const val weekDayCount = 7

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    currentYear: Int,
    currentMonth: Int,
    calendarList: List<List<CalendarItemData>>,
    selectedCalendarItemData: CalendarItemData? = null,
    onChangeMonth: (Boolean) -> Unit,
    onClickItem: (CalendarItemData?) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val itemSize = (screenWidth - (24 + 24)) / 7.0f

    var selectedPage by remember { mutableStateOf<Int?>(null) }
    var selectedDayOfMonth by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarHeader(
            currentYear = currentYear,
            currentMonth = currentMonth,
            onChangeMonth = onChangeMonth
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = ColorSecondary
        )
        HorizontalPager(state = pagerState) { page ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(
                        minHeight = (itemSize * 6 + 1 * 5).dp // 1 -> Divider
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val calendarRowCount = calendarList[page].size / weekDayCount

                for (week in 1..calendarRowCount) {
                    Row(
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        for (weekDay in 1..weekDayCount) {
                            Column {
                                val dayOfMonth = (week - 1) * weekDayCount + weekDay - 1
                                val isSelectedPage = page == selectedPage
                                val isSelectedDayOfMonth = dayOfMonth == selectedDayOfMonth

                                val calendarItem = calendarList[page][dayOfMonth]

                                Box(
                                    modifier = Modifier
                                        .noRippleClickable {
                                            if (!isSelectedPage || !isSelectedDayOfMonth) {
                                                println("jaehoLee", "select non-null")
                                                selectedPage = page
                                                selectedDayOfMonth = dayOfMonth

                                                onClickItem(calendarItem)
                                            } else {
                                                println("jaehoLee", "select null")
                                                selectedPage = null
                                                selectedDayOfMonth = null

                                                onClickItem(null)
                                            }
                                        }
                                ) {
                                    CalendarItem(
                                        isCurrentMonth = calendarItem.isCurrentMonth,
                                        dayOfMonth = calendarItem.date.split('-').getOrNull(2)?.toIntOrNull() ?: 0,
                                        color = when (weekDay) {
                                            1 -> ColorSunday
                                            7 -> ColorSaturday
                                            else -> Color.White
                                        }
                                    )

                                    if ((isSelectedPage && isSelectedDayOfMonth) ||
                                        (selectedCalendarItemData?.date == calendarItem.date)) {
                                        Box(
                                            modifier = Modifier
                                                .size(itemSize.dp)
                                                .background(
                                                    color = Color.White.copy(alpha = 0.5f)
                                                )
                                        )
                                    }
                                }
                                if (week in 1..<calendarRowCount) {
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .width(itemSize.dp),
                                        thickness = 1.dp,
                                        color = Color.White
                                    )
                                }
                            }
                            if (weekDay in 1..<weekDayCount) {
                                VerticalDivider(
                                    modifier = Modifier
                                        .height(itemSize.dp),
                                    thickness = 1.dp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    modifier: Modifier = Modifier,
    currentYear: Int,
    currentMonth: Int,
    onChangeMonth: (Boolean) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(10.dp)
//                    .fillMaxWidth()
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(
                            minWidth = 18.dp,
                            minHeight = 18.dp
                        )
                        .noRippleClickable {
                            onChangeMonth(true)
                        }
                ) {
                    Text(
                        text = "<",
                        color = ColorSecondary,
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.Medium,
                        fontFamily = fontFamily
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${currentYear}년",
                    color = Color.White,
                    fontSize = 24.dpToSp(),
                    fontWeight = FontWeight.Medium,
                    fontFamily = fontFamily
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${String.format("%02d", currentMonth)}월",
                    modifier = Modifier.defaultMinSize(
                        minWidth = 30.dp
                    ),
                    color = Color.White,
                    fontSize = 24.dpToSp(),
                    fontWeight = FontWeight.Medium,
                    fontFamily = fontFamily
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .defaultMinSize(
                            minWidth = 18.dp,
                            minHeight = 18.dp
                        )
                        .noRippleClickable {
                            onChangeMonth(false)
                        }
                ) {
                    Text(
                        text = ">",
                        color = ColorSecondary,
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.Medium,
                        fontFamily = fontFamily
                    )
                }
            }
        }
        DayOfWeekRow()
    }
}

@Composable
private fun DayOfWeekRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
    ) {
        WeekDay.entries.map { weekDay ->
            weekDay.value
        }.forEachIndexed { index, weekDay ->
            DayOfWeekRowItem(
                text = weekDay,
                color = when (index) {
                    0 -> ColorSunday
                    6 -> ColorSaturday
                    else -> Color.White
                }
            )

            if (index != WeekDay.entries.size - 1) {
                Spacer(modifier = Modifier.width(1.dp))
            }
        }
    }
}

@Composable
private fun DayOfWeekRowItem(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.White
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val itemSize = (screenWidth - (24 + 24)) / 7.0f

    Box(
        modifier = modifier
            .size(itemSize.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier
                .align(Alignment.Center),
            color = color,
            fontSize = 12.dpToSp(),
            fontFamily = fontFamily
        )
    }
}

@Composable
private fun CalendarItem(
    modifier: Modifier = Modifier,
    isCurrentMonth: Boolean,
    dayOfMonth: Int,
    color: Color
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val itemSize = (screenWidth - (24 + 24)) / 7.0f

    Box(
        modifier = modifier
            .size(itemSize.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(2.dp)
                .align(Alignment.TopEnd)
        ) {
            Text(
                text = "$dayOfMonth",
                color = if (isCurrentMonth) {
                    color
                } else {
                    color.copy(alpha = 0.3f)
                },
                fontSize = 11.dpToSp(),
                fontWeight = FontWeight.Light,
                fontFamily = fontFamily,
                lineHeight = 1.dpToSp()
            )
        }
    }
}