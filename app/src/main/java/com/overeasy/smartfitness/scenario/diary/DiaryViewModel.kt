package com.overeasy.smartfitness.scenario.diary

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.model.CalendarItem
import com.overeasy.smartfitness.module.CalendarManager
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import java.time.LocalDate
import java.time.YearMonth

@HiltViewModel
class DiaryViewModel @Inject constructor(

) : ViewModel() {
    private val _currentEndOfMonth = MutableStateFlow(YearMonth.now().atEndOfMonth())
    val currentEndOfMonth = _currentEndOfMonth.asStateFlow()

    private val _calendarList = mutableStateListOf<List<CalendarItem>>()
    val calendarList = _calendarList

    private val _calendarIndex = MutableStateFlow(0)
    val calendarIndex = _calendarIndex.asStateFlow()

    init {
        /**
         * 1970-1 -> 0
         * 1970-2 -> 1
         */
        viewModelScope.launch {
            launch (Dispatchers.Default) {
                initializeCalendar()
            }
        }
        onCollect()
    }

    private suspend fun initializeCalendar() {
        val calendarList = CalendarManager.getCalendarList()
        val currentFirstDayOfMonth = YearMonth.now().atDay(1)

        val currentCalendarOfMonthIndex = calendarList.indexOfFirst { monthList ->
            monthList.find { calendarItem ->
                val isActive = calendarItem.isActive
                val isCurrent = calendarItem.date == currentFirstDayOfMonth.run {
                    "$year/${String.format("%02d", monthValue)}/${String.format("%02d", dayOfMonth)}"
                }

                isActive && isCurrent
            } != null
        }

        println("jaehoLee", "index = $currentCalendarOfMonthIndex")

        _calendarIndex.value = currentCalendarOfMonthIndex
        _calendarList.addAll(calendarList)
    }

    private fun onCollect() {
//        viewModelScope.launch {
//            launch {
//                currentEndOfMonth.collectLatest { date ->
//                    refreshNewPagerData(date)
//                }
//            }
//            launch(Dispatchers.Default) {
//                isCalendarListEmpty.collectLatest { isEmpty ->
//                    println("jaehoLee", "isEmptyVM = $isEmpty")
//                }
//            }
//        }
    }

    fun onChangeMonth(isSwipedToLeft: Boolean) {
        val previousDate = currentEndOfMonth.value
        val newDate = if (isSwipedToLeft) {
            if (previousDate.monthValue != 1) {
                YearMonth.of(previousDate.year, previousDate.monthValue - 1).atEndOfMonth()
            } else {
                YearMonth.of(previousDate.year - 1, 12).atEndOfMonth()
            }
        } else {
            if (previousDate.monthValue != 12) {
                YearMonth.of(previousDate.year, previousDate.monthValue + 1).atEndOfMonth()
            } else {
                YearMonth.of(previousDate.year + 1, 1).atEndOfMonth()
            }
        }
        println("jaehoLee", newDate.run { "$year/$monthValue/$dayOfMonth" })
        _currentEndOfMonth.value = newDate
    }
}