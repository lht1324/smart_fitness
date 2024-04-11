package com.overeasy.smartfitness.scenario.workout.workout

import androidx.lifecycle.ViewModel
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

}