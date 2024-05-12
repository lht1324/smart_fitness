package com.overeasy.smartfitness.scenario.workout.result

import androidx.lifecycle.ViewModel
import com.overeasy.smartfitness.domain.workout.WorkoutRepository
import com.overeasy.smartfitness.domain.workout.model.workout.Menu
import com.overeasy.smartfitness.domain.workout.model.workout.NutritionAmount
import com.overeasy.smartfitness.domain.workout.model.workout.SetCount
import com.overeasy.smartfitness.domain.workout.model.workout.Score
import com.overeasy.smartfitness.domain.workout.model.workout.Workout
import com.overeasy.smartfitness.domain.workout.model.workout.WorkoutResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WorkoutResultViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    private val _workoutResult = MutableStateFlow(
        WorkoutResult(
            workoutList = listOf(
                Workout(
                    name = "벤치 프레스",
                    setCountList = listOf(
                        SetCount(
                            set = 1,
                            count = 10
                        ),
                        SetCount(
                            set = 2,
                            count = 15
                        ),
                        SetCount(
                            set = 3,
                            count = 20
                        ),
                        SetCount(
                            set = 4,
                            count = 15
                        ),
                    ),
                    calorieUsage = 3
                ),
                Workout(
                    name = "스쿼트",
                    setCountList = listOf(
                        SetCount(
                            set = 1,
                            count = 15
                        ),
                        SetCount(
                            set = 2,
                            count = 30
                        ),
                        SetCount(
                            set = 3,
                            count = 10
                        ),
                        SetCount(
                            set = 4,
                            count = 20
                        ),
                    ),
                    calorieUsage = 3
                )
            ),
            workoutScoreList = listOf(
                Score(
                    name = "Cool",
                    score = 11
                ),
                Score(
                    name = "Good",
                    score = 6
                ),
                Score(
                    name = "Not Good",
                    score = 3
                ),
            ),
            workoutTotalScore = 2400,
//            menuList = null
            menuList = listOf(
                Menu(
                    name = "갈비찜",
                    nutritionAmountList = listOf(
                        NutritionAmount(
                            name = "탄수화물",
                            amount = 10
                        ),
                        NutritionAmount(
                            name = "단백질",
                            amount = 11
                        ),
                        NutritionAmount(
                            name = "지방",
                            amount = 12
                        ),
                        NutritionAmount(
                            name = "당",
                            amount = 13
                        ),
                        NutritionAmount(
                            name = "나트륨",
                            amount = 14
                        ),
                        NutritionAmount(
                            name = "콜레스테롤",
                            amount = 15
                        )
                    )
                ),
                Menu(
                    name = "스테이크",
                    nutritionAmountList = listOf(
                        NutritionAmount(
                            name = "탄수화물",
                            amount = 10
                        ),
                        NutritionAmount(
                            name = "단백질",
                            amount = 12
                        ),
                        NutritionAmount(
                            name = "지방",
                            amount = 14
                        ),
                        NutritionAmount(
                            name = "당",
                            amount = 15
                        ),
                        NutritionAmount(
                            name = "나트륨",
                            amount = 13
                        ),
                        NutritionAmount(
                            name = "콜레스테롤",
                            amount = 11
                        )
                    )
                )
            )
        )
    )

    val workoutResult = _workoutResult.asStateFlow()
}