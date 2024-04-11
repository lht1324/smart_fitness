package com.overeasy.smartfitness.scenario.diet.diet

import androidx.lifecycle.ViewModel
import com.overeasy.smartfitness.domain.diet.DietRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DietViewModel @Inject constructor(
    private val dietRepository: DietRepository
) : ViewModel() {
}