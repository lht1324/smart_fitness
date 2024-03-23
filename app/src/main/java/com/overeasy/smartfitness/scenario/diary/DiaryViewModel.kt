package com.overeasy.smartfitness.scenario.diary

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.overeasy.smartfitness.model.CalendarItem
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import java.time.LocalDate
import java.time.YearMonth

@HiltViewModel
class DiaryViewModel @Inject constructor(

) : ViewModel() {
    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate = _currentDate.asSharedFlow()

    private val _calendarList = mutableStateListOf<List<CalendarItem>>()
//    private val _calendarList = mutableStateListOf<CalendarItem>()
    val calendarList = _calendarList

    init {
//        _calendarList.addAll(getCalendarOfMonth(YearMonth.now().atEndOfMonth()))
        _calendarList.addAll(getNewPagerData())
        calendarList.toList().forEachIndexed { index, list ->
            list.forEach { item ->
                println("jaehoLee", "[${index}]: ${item.date}")
            }
        }
    }

    fun onClickCalendarLeftButton() {

    }

    fun onClickCalendarRightButton() {

    }

    private fun getNewPagerData(): List<List<CalendarItem>> {
        val currentMonth = YearMonth.now().atEndOfMonth() // 변화의 결과

        // 스와이프가 끝나고 현재 월의 마지막 날이 기준이 되어야 한다
        // 그 날을 기준으로 새로 뽑기만 하면 된다
        return listOf(
            getCalendarOfMonth(
                if (currentMonth.monthValue <= 2) {
                    YearMonth.of(currentMonth.year - 1, currentMonth.monthValue + 10).atEndOfMonth().apply {
                        this
                        println("jaehoLee", run {
                            "1, ${year}/${monthValue}/${dayOfMonth}"
                        })
                    }
                } else {
                    YearMonth.of(currentMonth.year, currentMonth.monthValue - 2).atEndOfMonth().apply {
                        println("jaehoLee", run {
                            "1, ${year}/${monthValue}/${dayOfMonth}"
                        })
                    }
                }
            ),
            getCalendarOfMonth(
                if (currentMonth.monthValue <= 1) {
                    YearMonth.of(currentMonth.year - 1, currentMonth.monthValue + 11).atEndOfMonth().apply {
                        println("jaehoLee", run {
                            "2, ${year}/${monthValue}/${dayOfMonth}"
                        })
                    }
                } else {
                    YearMonth.of(currentMonth.year, currentMonth.monthValue - 1).atEndOfMonth().apply {
                        println("jaehoLee", run {
                            "2, ${year}/${monthValue}/${dayOfMonth}"
                        })
                    }
                }
            ),
            getCalendarOfMonth(currentMonth),
            getCalendarOfMonth(
                if (currentMonth.monthValue >= 12) {
                    YearMonth.of(currentMonth.year + 1, currentMonth.monthValue - 11).atEndOfMonth().apply {
                        println("jaehoLee", run {
                            "3, ${year}/${monthValue}/${dayOfMonth}"
                        })
                    }
                } else {
                    YearMonth.of(currentMonth.year, currentMonth.monthValue + 1).atEndOfMonth().apply {
                        println("jaehoLee", run {
                            "3, ${year}/${monthValue}/${dayOfMonth}"
                        })
                    }
                }
            ),
            getCalendarOfMonth(
                if (currentMonth.monthValue >= 11) {
                    YearMonth.of(currentMonth.year + 1, currentMonth.monthValue - 10).atEndOfMonth().apply {
                        println("jaehoLee", run {
                            "4, ${year}/${monthValue}/${dayOfMonth}"
                        })
                    }
                } else {
                    YearMonth.of(currentMonth.year, currentMonth.monthValue + 2).atEndOfMonth().apply {
                        println("jaehoLee", run {
                            "4, ${year}/${monthValue}/${dayOfMonth}"
                        })
                    }
                }
            )
        )
    }

    private fun getCalendarOfMonth(endOfCurrentMonth: LocalDate): List<CalendarItem> {
//        val endOfCurrentMonth = YearMonth.now().atEndOfMonth()
        val startOfMonth = LocalDate.of(endOfCurrentMonth.year, endOfCurrentMonth.monthValue, 1)
//        val endOfMonth = YearMonth.of(2023, 1).atEndOfMonth()
//        val startOfMonth = LocalDate.of(endOfMonth.year, endOfMonth.monthValue, 1)
        val endOfLastMonth = if (startOfMonth.monthValue != 1) {
            YearMonth.of(startOfMonth.year, startOfMonth.monthValue - 1).atEndOfMonth()
        } else {
            LocalDate.of(startOfMonth.year - 1, 12, 31)
        }
        val startOfNextMonth = if (endOfCurrentMonth.monthValue != 12) {
            YearMonth.of(startOfMonth.year, startOfMonth.monthValue + 1).atDay(1)
        } else {
            LocalDate.of(startOfMonth.year + 1, 1, 1)
        }

        val lastMonthCount = startOfMonth.dayOfWeek.value - 1
        val nextMonthCount = 7 - endOfCurrentMonth.dayOfWeek.value
        val dateList = arrayListOf<CalendarItem>()

        val getFormattedNumber: (Int) -> String = { number: Int ->
            String.format("%02d", number)
        }

        for (i in 1..lastMonthCount + endOfCurrentMonth.dayOfMonth + nextMonthCount) {
            val calendarItem = CalendarItem(
                isActive = i in (lastMonthCount + 1)..(endOfCurrentMonth.dayOfMonth + lastMonthCount),
                date = when (i) {
                    in 1..lastMonthCount -> "${endOfLastMonth.year}" +
                            "/${getFormattedNumber(endOfLastMonth.monthValue)}" +
                            "/${getFormattedNumber(endOfLastMonth.dayOfMonth - (lastMonthCount - i))}"

                    in lastMonthCount + 1..endOfCurrentMonth.dayOfMonth + lastMonthCount -> "${startOfMonth.year}" +
                            "/${getFormattedNumber(startOfMonth.monthValue)}" +
                            "/${getFormattedNumber(i - lastMonthCount)}"

                    else -> "${startOfNextMonth.year}" + // i >= endOfMonth.dayOfMonth + lastMonthCount + 1
                            "/${getFormattedNumber(startOfNextMonth.monthValue)}" +
                            "/${getFormattedNumber(i - (endOfCurrentMonth.dayOfMonth + lastMonthCount))}"
                }
            )
            dateList.add(calendarItem)
            // 5개를 넣는다
            // 이동하고 난 뒤 destination이 2번 인덱스인 배열을 새로 만들어 뷰페이저를 갈아준다
        }

        return dateList
    }
}