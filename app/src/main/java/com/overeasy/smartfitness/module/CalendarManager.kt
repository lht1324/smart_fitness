package com.overeasy.smartfitness.module

import com.overeasy.smartfitness.model.diary.CalendarItemData
import java.time.LocalDate
import java.time.YearMonth

object CalendarManager {
    fun getCalendarList(): List<List<CalendarItemData>> {
        val calendarList = arrayListOf<List<CalendarItemData>>()
        
        val startYear = 1970
        val endYear = 2037

        for (year in startYear..endYear) {
            for (month in 1..12) {
                val monthList = arrayListOf<CalendarItemData>()
                
                val yearMonth = YearMonth.of(year, month)
                val firstDayOfMonth = yearMonth.atDay(1)
                val lastDayOfMonth = yearMonth.atEndOfMonth()

                val lastDayOfLastMonth = if (month != 1) {
                    YearMonth.of(year, month - 1).atEndOfMonth()
                } else {
                    LocalDate.of(year - 1, 12, 31)
                }
                val firstDayOfNextMonth = if (month != 12) {
                    YearMonth.of(year, month + 1).atDay(1)
                } else {
                    LocalDate.of(year + 1, 1, 1)
                }

                val lastMonthLastWeekCount = firstDayOfMonth.dayOfWeek.value
                val nextMonthFirstWeekCount = if (lastDayOfMonth.dayOfWeek.value < 7) {
                    -(lastDayOfMonth.dayOfWeek.value - 6)
                } else {
                    lastDayOfMonth.dayOfWeek.value - 1
                }

                val getFormattedNumber: (Int) -> String = { number: Int ->
                    String.format("%02d", number)
                }

                for (day in 1..lastMonthLastWeekCount + lastDayOfMonth.dayOfMonth + nextMonthFirstWeekCount) {
                    val calendarItemData = CalendarItemData(
                        isCurrentMonth = day in (lastMonthLastWeekCount + 1)..(lastDayOfMonth.dayOfMonth + lastMonthLastWeekCount),
                        date = when (day) {
                            in 1..lastMonthLastWeekCount -> "${lastDayOfLastMonth.year}" +
                                    "-${getFormattedNumber(lastDayOfLastMonth.monthValue)}" +
                                    "-${getFormattedNumber(lastDayOfLastMonth.dayOfMonth - (lastMonthLastWeekCount - day))}"

                            in lastMonthLastWeekCount + 1..lastDayOfMonth.dayOfMonth + lastMonthLastWeekCount -> "$year" +
                                    "-${getFormattedNumber(month)}" +
                                    "-${getFormattedNumber(day - lastMonthLastWeekCount)}"

                            else -> "${firstDayOfNextMonth.year}" + // i >= endOfMonth.dayOfMonth + lastMonthCount + 1
                                    "-${getFormattedNumber(firstDayOfNextMonth.monthValue)}" +
                                    "-${getFormattedNumber(day - (lastDayOfMonth.dayOfMonth + lastMonthLastWeekCount))}"
                        }
                    )
                    monthList.add(calendarItemData)
                }
                
                calendarList.add(monthList)
            }
        }

        return calendarList
    }
}