package com.overeasy.smartfitness.model.diary

data class CalendarItem(
    val isCurrentMonth: Boolean,
    val isWorkoutDay: Boolean = false,
    val date: String,
    val daySetCount: Int = 0,
    val dayCalorieUsage: Int = 0,
    val dayCalorieIncome: Int? = null
)