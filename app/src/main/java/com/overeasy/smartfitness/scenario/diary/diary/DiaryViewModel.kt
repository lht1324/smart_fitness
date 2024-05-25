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

    private val _selectedCalendarItemData = MutableStateFlow<CalendarItemData?>(null)
    val selectedCalendarItemData = _selectedCalendarItemData.asStateFlow()

    private val _noteIdList = MutableStateFlow<List<Int>>(listOf())
    val noteIdList = _noteIdList.asStateFlow()

    private val _totalPerfect = MutableStateFlow(-1)
    val totalPerfect = _totalPerfect.asStateFlow()
    private val _totalGood = MutableStateFlow(-1)
    val totalGood = _totalGood.asStateFlow()
    private val _totalNotGood = MutableStateFlow(-1)
    val totalNotGood = _totalNotGood.asStateFlow()
    private val _totalScore = MutableStateFlow(-1)
    val totalScore = _totalScore.asStateFlow()
    private val _totalKcal = MutableStateFlow(0)
    val totalKcal = _totalKcal.asStateFlow()

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

    fun onChangeSelectedCalendarData(calendarData: CalendarItemData?) {
        _selectedCalendarItemData.value = calendarData
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
                val noteList = res.result!!.noteList.filter { note ->
                    val isInvalidNote = note.totalPerfect == 0 &&
                            note.totalGood == 0 &&
                            note.totalBad == 0 &&
                            note.totalScore == 0 &&
                            (note.totalKcal == null || note.totalKcal == 0)

                    !isInvalidNote
                }

                _noteIdList.value = noteList.map { note ->
                    note.noteId
                }

                _totalPerfect.value = noteList.sumOf { note ->
                    note.totalPerfect
                }
                _totalGood.value = noteList.sumOf { note ->
                    note.totalGood
                }
                _totalNotGood.value = noteList.sumOf { note ->
                    note.totalBad
                }
                _totalScore.value = noteList.sumOf { note ->
                    note.totalScore
                }

                _totalKcal.value = noteList.filter { note ->
                    note.totalKcal != null
                }.sumOf { note ->
                    note.totalKcal!!
                }

                println("jaehoLee", "totalPerfect = ${totalPerfect.value}")
                println("jaehoLee", "totalGood = ${totalGood.value}")
                println("jaehoLee", "totalNotGood = ${totalNotGood.value}")
                println("jaehoLee", "totalScore = ${totalScore.value}")
                println("jaehoLee", "totalKcal = ${totalKcal.value}")

//                _workoutDiaryItemList.clear()
//                _workoutDiaryItemList.addAll(res.result!!.noteList)
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