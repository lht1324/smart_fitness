package com.overeasy.smartfitness.module

import com.overeasy.smartfitness.model.CalendarItem
import java.time.LocalDate
import java.time.YearMonth

object CalendarManager {
    fun getCalendarList(): List<List<CalendarItem>> {
        val calendarList = arrayListOf<List<CalendarItem>>()
        
        val startYear = 1970
        val endYear = 2037

        for (year in startYear..endYear) {
            for (month in 1..12) {
                val monthList = arrayListOf<CalendarItem>()
                
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

                val lastMonthLastWeekCount = firstDayOfMonth.dayOfWeek.value - 1
                val nextMonthFirstWeekCount = 7 - lastDayOfMonth.dayOfWeek.value

                val getFormattedNumber: (Int) -> String = { number: Int ->
                    String.format("%02d", number)
                }

                for (day in 1..lastMonthLastWeekCount + lastDayOfMonth.dayOfMonth + nextMonthFirstWeekCount) {
                    val calendarItem = CalendarItem(
                        isActive = day in (lastMonthLastWeekCount + 1)..(lastDayOfMonth.dayOfMonth + lastMonthLastWeekCount),
                        date = when (day) {
                            in 1..lastMonthLastWeekCount -> "${lastDayOfLastMonth.year}" +
                                    "/${getFormattedNumber(lastDayOfLastMonth.monthValue)}" +
                                    "/${getFormattedNumber(lastDayOfLastMonth.dayOfMonth - (lastMonthLastWeekCount - day))}"

                            in lastMonthLastWeekCount + 1..lastDayOfMonth.dayOfMonth + lastMonthLastWeekCount -> "$year" +
                                    "/${getFormattedNumber(month)}" +
                                    "/${getFormattedNumber(day - lastMonthLastWeekCount)}"

                            else -> "${firstDayOfNextMonth.year}" + // i >= endOfMonth.dayOfMonth + lastMonthCount + 1
                                    "/${getFormattedNumber(firstDayOfNextMonth.monthValue)}" +
                                    "/${getFormattedNumber(day - (lastDayOfMonth.dayOfMonth + lastMonthLastWeekCount))}"
                        }
                    )
                    monthList.add(calendarItem)
                }
                
                calendarList.add(monthList)
            }
        }

        return calendarList
    }
}