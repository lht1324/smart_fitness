package com.overeasy.smartfitness.scenario.diary.diary

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.domain.diary.DiaryRepository
import com.overeasy.smartfitness.domain.workout.model.diary.Note
import com.overeasy.smartfitness.model.diary.CalendarItemData
import com.overeasy.smartfitness.module.CalendarManager
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import java.time.YearMonth

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    private val currentYearMonth = MutableStateFlow<YearMonth?>(null)
    val currentYear = currentYearMonth.filterNotNull().map { yearMonth ->
        yearMonth.year
    }
    val currentMonth = currentYearMonth.filterNotNull().map { yearMonth ->
        yearMonth.monthValue
    }

    private val _calendarList = mutableStateListOf<List<CalendarItemData>>()
    val calendarList = _calendarList

    private val _calendarIndex = MutableStateFlow<Int?>(null)
    val calendarIndex = _calendarIndex.asStateFlow()

    private val _selectedDiaryItemList = mutableStateListOf<Note>()
    val selectedDiaryItemList = _selectedDiaryItemList

    init {
        viewModelScope.launch {
            launch (Dispatchers.Default) {
                initializeCalendar()
            }
        }
    }

    private suspend fun initializeCalendar() = withContext(Dispatchers.Default) {
        val calendarList = CalendarManager.getCalendarList().map { list ->
            list.map { calendarItem ->
                val split = calendarItem.date.split('-')
                val year = split[0].toInt()
                val month = split[1].toInt()
                val day = split[2].toInt()

                calendarItem.copy(
                    daySetCount = year,
                    dayCalorieUsage = month,
                    dayCalorieIncome = day
                )
            }
        }
        val currentFirstDayOfMonth = YearMonth.now().atDay(1)

        val currentCalendarOfMonthIndex = calendarList.indexOfFirst { monthList ->
            monthList.find { calendarItem ->
                val isCurrentMonth = calendarItem.isCurrentMonth
                val isCurrent = calendarItem.date == currentFirstDayOfMonth.run {
                    "$year-${String.format("%02d", monthValue)}-${String.format("%02d", dayOfMonth)}"
                }

                isCurrentMonth && isCurrent
            } != null
        }

        currentYearMonth.value = YearMonth.now()
        _calendarIndex.value = currentCalendarOfMonthIndex
        _calendarList.addAll(calendarList)
    }

    fun onChangeMonth(isSwipedToLeft: Boolean) {
        if (currentYearMonth.value != null) {
            val previousDate = currentYearMonth.value!!
            val newYearMonth = if (isSwipedToLeft) {
                if (previousDate.monthValue != 1) {
                    YearMonth.of(previousDate.year, previousDate.monthValue - 1)
                } else {
                    YearMonth.of(previousDate.year - 1, 12)
                }
            } else {
                if (previousDate.monthValue != 12) {
                    YearMonth.of(previousDate.year, previousDate.monthValue + 1)
                } else {
                    YearMonth.of(previousDate.year + 1, 1)
                }
            }
            currentYearMonth.value = newYearMonth
        }
    }

    fun onClickCalendarItem(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            requestGetDiary(date)
        }
    }

    private suspend fun requestGetDiary(date: String) {
        ApiRequestHelper.makeRequest {
            diaryRepository.getDiary(date)
        }.onSuccess { res ->
            if (!(res.result?.noteList.isNullOrEmpty())) {
                _selectedDiaryItemList.clear()
                _selectedDiaryItemList.addAll(res.result!!.noteList)
            }
        }.onFailure { res ->
            println("jaehoLee", "onFailure: ${res.code}, ${res.message}")
        }.onError { throwable ->
            println("jaehoLee", "onError: ${throwable.message}")
        }
    }

    sealed class DiaryUiEvent {

    }
}