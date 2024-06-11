package com.overeasy.smartfitness.scenario.diary.diary

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.domain.base.makeRequest
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.dto.res.note.toEntity
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
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
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

    private val _perfectCount = MutableStateFlow(-1)
    val prefectCount = _perfectCount.asStateFlow()
    private val _goodCount = MutableStateFlow(-1)
    val goodCount = _goodCount.asStateFlow()
    private val _notGoodCount = MutableStateFlow(-1)
    val notGoodCount = _notGoodCount.asStateFlow()
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

    fun clearDiaryData() {
        _noteIdList.value = listOf()

        _perfectCount.value = -1
        _goodCount.value = -1
        _notGoodCount.value = -1
        _totalScore.value = -1

        _totalKcal.value = 0
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
            requestGetWorkoutNoteList(date)
        }
    }

    private suspend fun requestGetWorkoutNoteList(date: String) {
        makeRequest {
            workoutRepository.getWorkoutNoteList(date)
        }.onSuccess { res ->
            if (!(res.result?.noteList.isNullOrEmpty())) {
                val diaryList = res.result!!.noteList.map { note ->
                    note.toEntity()
                }.filter { diaryListItem ->
                    diaryListItem.run {
                        val isInvalidNote = perfectCount == 0 &&
                                goodCount == 0 &&
                                notGoodCount == 0 &&
                                totalScore == 0 &&
                                (totalKcal == null || totalKcal == 0)

                        !isInvalidNote
                    }
                }

                _noteIdList.value = diaryList.map { diaryListItem ->
                    diaryListItem.noteId
                }

                _perfectCount.value = diaryList.sumOf { diaryListItem ->
                    diaryListItem.perfectCount
                }
                _goodCount.value = diaryList.sumOf { diaryListItem ->
                    diaryListItem.goodCount
                }
                _notGoodCount.value = diaryList.sumOf { diaryListItem ->
                    diaryListItem.notGoodCount
                }
                _totalScore.value = diaryList.sumOf { diaryListItem ->
                    diaryListItem.totalScore
                }

                _totalKcal.value = diaryList.filter { diaryListItem ->
                    diaryListItem.totalKcal != null
                }.sumOf { diaryListItem ->
                    diaryListItem.totalKcal!!
                }
            } else {
                clearDiaryData()
            }
        }.onFailure { res ->
            clearDiaryData()

            println("jaehoLee", "onFailure in requestGetWorkoutNoteList(): ${res.code}, ${res.message}")
        }.onError { throwable ->
            clearDiaryData()

            println("jaehoLee", "onError in requestGetWorkoutNoteList(): ${throwable.message}")
        }
    }

    sealed class DiaryUiEvent {

    }
}