package com.overeasy.smartfitness.scenario.setting.myinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.domain.setting.entity.PutUsersReq
import com.overeasy.smartfitness.domain.setting.model.UserData
import com.overeasy.smartfitness.isLettersOrDigits
import com.overeasy.smartfitness.isLettersOrDigitsIncludeKorean
import com.overeasy.smartfitness.model.register.RegisterBodyInfo
import com.overeasy.smartfitness.model.register.RegisterTasteInfo
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {
    private val _myInfoUiEvent = MutableSharedFlow<MyInfoUiEvent>()
    val myInfoUiEvent = _myInfoUiEvent.asSharedFlow()

    // 임시 로직, 당연히 지우는 게 맞는데 내일 시연이라 넣음
    private val userDataFlow = MutableStateFlow<UserData?>(null)

    private val _nickname = MutableStateFlow("")

    private val _bodyInfo = MutableStateFlow(
        RegisterBodyInfo(
            age = null,
            height = null,
            weight = null
        )
    )

    private val _tasteInfo = MutableStateFlow(
        RegisterTasteInfo(
            spicyPreference = null,
            meatConsumption = null,
            tastePreference = null,
            activityLevel = null,
            preferenceTypeFood = null,
        )
    )

    val nickname = _nickname.asStateFlow()

    val bodyInfo = _bodyInfo.asStateFlow()

    val tasteInfo = _tasteInfo.asStateFlow()

    val isNicknameInvalid = _nickname.map { nickname ->
        !(nickname.length <= 8 && isLettersOrDigitsIncludeKorean(nickname)) && nickname.isNotEmpty()
    }

    val isAgeInvalid = _bodyInfo.map { bodyInfo ->
        val age = (bodyInfo.age?.toIntOrNull() ?: -1)

        age !in 0..< 100 && bodyInfo.age?.isNotEmpty() == true
    }
    val isHeightInvalid = _bodyInfo.map { bodyInfo ->
        val height = (bodyInfo.height?.toFloatOrNull() ?: 0.0f)

        height !in 120.0f..250.0f && bodyInfo.height?.isNotEmpty() == true
    }
    val isWeightInvalid = _bodyInfo.map { bodyInfo ->
        val weight = (bodyInfo.weight?.toFloatOrNull() ?: 0.0f)

        weight !in 30.0f..200.0f && bodyInfo.weight?.isNotEmpty() == true
    }

    private val isClickedOnChangeNickname = MutableStateFlow(false)

    private val isClickedOnChangeBodyInfo = MutableStateFlow(false)

    private val isClickedOnChangeTasteInfo = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                ApiRequestHelper.makeRequest {
                    settingRepository.getUsers(MainApplication.appPreference.userId)
                }.onSuccess { res ->
                    val userData = res.result

                    userDataFlow.value = userData

                    _nickname.value = userData?.nickname ?: ""

                    _bodyInfo.value = RegisterBodyInfo(
                        age = userData?.age?.toString(),
                        height = userData?.height?.toString(),
                        weight = userData?.weight?.toString()
                    )

                    _tasteInfo.value = RegisterTasteInfo(
                        spicyPreference = userData?.spicyPreference,
                        meatConsumption = userData?.meatConsumption,
                        tastePreference = userData?.tastePreference,
                        activityLevel = userData?.activityLevel,
                        preferenceTypeFood = userData?.preferenceTypeFood
                    )
                }.onFailure { res ->
                    println("jaehoLee", "onFailure: $res")
                }.onError { throwable ->
                    println("jaehoLee", "onError: ${throwable.message}")
                }
            }
            launch(Dispatchers.IO) {
                combine(isClickedOnChangeNickname, nickname) { isClicked, nickname ->
                    isClicked to nickname
                }.filter { (isClicked, _) ->
                    isClicked
                }.map { (_, nickname) ->
                    nickname
                }.collectLatest { nickname ->
                    requestPutUsers(
                        nickname = nickname,
                        age = userDataFlow.value?.age,
                        height = userDataFlow.value?.height,
                        weight = userDataFlow.value?.weight,
                        spicyPreference = userDataFlow.value?.spicyPreference,
                        meatConsumption = userDataFlow.value?.meatConsumption,
                        tastePreference = userDataFlow.value?.tastePreference,
                        activityLevel = userDataFlow.value?.activityLevel,
                        preferenceTypeFood = userDataFlow.value?.preferenceTypeFood,
                    )
                }
            }
            launch(Dispatchers.IO) {
                combine(isClickedOnChangeBodyInfo, bodyInfo) { isClicked, bodyInfo ->
                    isClicked to bodyInfo
                }.filter { (isClicked, _) ->
                    isClicked
                }.map { (_, bodyInfo) ->
                    bodyInfo
                }.collectLatest { bodyInfo ->
                    requestPutUsers(
                        nickname = userDataFlow.value?.nickname,
                        age = bodyInfo.age?.toIntOrNull(),
                        height = bodyInfo.height?.toFloatOrNull(),
                        weight = bodyInfo.weight?.toFloatOrNull(),
                        spicyPreference = userDataFlow.value?.spicyPreference,
                        meatConsumption = userDataFlow.value?.meatConsumption,
                        tastePreference = userDataFlow.value?.tastePreference,
                        activityLevel = userDataFlow.value?.activityLevel,
                        preferenceTypeFood = userDataFlow.value?.preferenceTypeFood,
                    )
                }
            }
            launch(Dispatchers.IO) {
                combine(isClickedOnChangeTasteInfo, tasteInfo) { isClicked, tasteInfo ->
                    isClicked to tasteInfo
                }.filter { (isClicked, _) ->
                    isClicked
                }.map { (_, tasteInfo) ->
                    tasteInfo
                }.collectLatest { tasteInfo ->
                    requestPutUsers(
                        nickname = userDataFlow.value?.nickname,
                        age = userDataFlow.value?.age,
                        height = userDataFlow.value?.height,
                        weight = userDataFlow.value?.weight,
                        spicyPreference = tasteInfo.spicyPreference,
                        meatConsumption = tasteInfo.meatConsumption,
                        tastePreference = tasteInfo.tastePreference,
                        activityLevel = tasteInfo.activityLevel,
                        preferenceTypeFood = tasteInfo.preferenceTypeFood,
                    )
                }
            }
        }
    }

    fun onClickChangeNickname() {
        isClickedOnChangeNickname.value = true
    }

    fun onClickChangeBodyInfo() {
        isClickedOnChangeBodyInfo.value = true
    }

    fun onClickChangeTasteInfo() {
        isClickedOnChangeTasteInfo.value = true
    }

    private suspend fun requestPutUsers(
        nickname: String? = null,
        age: Int? = null,
        height: Float? = null,
        weight: Float? = null,
        spicyPreference: Int? = null,
        meatConsumption: Boolean? = null,
        tastePreference: String? = null,
        activityLevel: Int? = null,
        preferenceTypeFood: String? = null
    ) {
        ApiRequestHelper.makeRequest {
            println("jaehoLee", "spicy = $spicyPreference, meat = $meatConsumption, taste = $tastePreference, act = $activityLevel, food = $preferenceTypeFood")
            settingRepository.putUsers(
                PutUsersReq(
                    id = MainApplication.appPreference.userId,
                    nickname = nickname,
                    height = height,
                    weight = weight,
                    age = age,
                    spicyPreference = spicyPreference,
                    meatConsumption = meatConsumption,
                    tastePreference = tastePreference,
                    activityLevel = activityLevel,
                    preferenceTypeFood = preferenceTypeFood
                )
            )
        }.onSuccess { res ->
            _myInfoUiEvent.emit(MyInfoUiEvent.OnSuccessChangeInfo)

            isClickedOnChangeNickname.value = false
            isClickedOnChangeBodyInfo.value = false
            isClickedOnChangeTasteInfo.value = false
        }.onFailure { res ->
            _myInfoUiEvent.emit(MyInfoUiEvent.OnFailureChangeInfo)
            println("jaehoLee", "onFailure: $res")

            isClickedOnChangeNickname.value = false
            isClickedOnChangeBodyInfo.value = false
            isClickedOnChangeTasteInfo.value = false
        }.onError { throwable ->
            _myInfoUiEvent.emit(MyInfoUiEvent.OnFailureChangeInfo)
            println("jaehoLee", "onError: ${throwable.message}")

            isClickedOnChangeNickname.value = false
            isClickedOnChangeBodyInfo.value = false
            isClickedOnChangeTasteInfo.value = false
        }
    }

    fun onChangeNickname(value: String) {
        _nickname.value = value
    }

    fun onChangeAge(value: String) {
        if (value.toIntOrNull() != null || value.isEmpty()) {
            _bodyInfo.value = bodyInfo.value.copy(
                age = if (value.toIntOrNull() != null && value.startsWith("0")) {
                    value.toInt().toString()
                } else {
                    value
                }
            )
        }
    }

    fun onChangeHeight(value: String) {
        if ((value.toFloatOrNull() != null || value.isEmpty())) {
            _bodyInfo.value = bodyInfo.value.copy(
                height = if (value.toFloatOrNull() != null && value.startsWith("0")) {
                    if (value.toFloat() % 1 != 0.0f) {
                        ((value.toFloat() * 100.0f).roundToInt() / 100.0f).toString()
                    } else {
                        value.toInt().toString()
                    }
                } else {
                    if (value.isNotEmpty() && (value.toFloat() % 1 != 0.0f)) {
                        ((value.toFloat() * 100.0f).roundToInt() / 100.0f).toString()
                    } else {
                        value
                    }
                }
            )
        }
    }

    fun onChangeWeight(value: String) {
        if (value.toFloatOrNull() != null || value.isEmpty()) {
            _bodyInfo.value = bodyInfo.value.copy(
                weight = if (value.toFloatOrNull() != null && value.startsWith("0")) {
                    if (value.toFloat() % 1 != 0.0f) {
                        ((value.toFloat() * 100.0f).roundToInt() / 100.0f).toString()
                    } else {
                        value.toInt().toString()
                    }
                } else {
                    if (value.isNotEmpty() && (value.toFloat() % 1 != 0.0f)) {
                        ((value.toFloat() * 100.0f).roundToInt() / 100.0f).toString()
                    } else {
                        value
                    }
                }
            )
        }
    }

    fun onChangeSpicyPreference(value: Int) {
        _tasteInfo.value = tasteInfo.value.copy(
            spicyPreference = value
        )
    }

    fun onChangeMeatConsumption(value: Boolean) {
        _tasteInfo.value = tasteInfo.value.copy(
            meatConsumption = value
        )
    }

    fun onChangeTastePreference(value: String) {
        println("jaehoLee", "taste = $value")
        _tasteInfo.value = tasteInfo.value.copy(
            tastePreference = value
        )
    }

    fun onChangeActivityLevel(value: Int) {
        _tasteInfo.value = tasteInfo.value.copy(
            activityLevel = value
        )
    }

    fun onChangePreferenceTypeFood(value: String) {
        println("jaehoLee", "preference = $value")
        _tasteInfo.value = tasteInfo.value.copy(
            preferenceTypeFood = value
        )
    }

    sealed class MyInfoUiEvent {
        data object OnSuccessChangeInfo : MyInfoUiEvent()
        data object OnFailureChangeInfo : MyInfoUiEvent()
    }
}