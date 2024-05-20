package com.overeasy.smartfitness.scenario.setting.register

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.api.ApiRequestHelper
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.domain.setting.entity.PostUsersLoginReq
import com.overeasy.smartfitness.domain.setting.entity.PostUsersSignUpReq
import com.overeasy.smartfitness.isLettersOrDigits
import com.overeasy.smartfitness.isLettersOrDigitsIncludeKorean
import com.overeasy.smartfitness.model.register.RegisterBodyInfo
import com.overeasy.smartfitness.model.register.RegisterTasteInfo
import com.overeasy.smartfitness.println
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToInt

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {
    private val _registerUiEvent = MutableSharedFlow<RegisterUiEvent>()
    val registerUiEvent = _registerUiEvent.asSharedFlow()

    private val _id = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    private val _nickname = MutableStateFlow("")

    private val _bodyInfo = MutableStateFlow(
        RegisterBodyInfo(
            age = null,
            height = null,
            weight = null,
            gender = null
        )
    )

    private val tasteInfo = MutableStateFlow(
        RegisterTasteInfo(
            spicyPreference = null,
            meatConsumption = null,
            tastePreference = null,
            activityLevel = null,
            preferenceTypeFood = null,
            preferenceFoods = null
        )
    )

    val id = _id.asStateFlow()
    val password = _password.asStateFlow()

    val nickname = _nickname.asStateFlow()

    val bodyInfo = _bodyInfo.asStateFlow()

    private val essentialInfo = combine(id, password, nickname) { id, password, nickname ->
        Triple(id, password, nickname)
    }

    private val nonEssentialInfo = combine(bodyInfo, tasteInfo) { bodyInfo, tasteInfo ->
        bodyInfo to tasteInfo
    }.map { (bodyInfo, tasteInfo) ->
        val (age, height, weight) = bodyInfo
        val (spicyPreference, meatConsumption, tastePreference, activityLevel, preferenceTypeFood) = tasteInfo

        val isSkippedBodyInfoInput = age.isNullOrEmpty() || height.isNullOrEmpty() || weight.isNullOrEmpty()
        val isSkippedTasteInfoInput = spicyPreference == null &&
                meatConsumption == null &&
                tastePreference == null &&
                preferenceTypeFood == null

        bodyInfo to tasteInfo
//        if (!isSkippedBodyInfoInput && !isSkippedTasteInfoInput) {
//            bodyInfo to tasteInfo
//        } else if (isSkippedBodyInfoInput && !isSkippedTasteInfoInput) {
//            null to tasteInfo
//        } else if (!isSkippedBodyInfoInput && isSkippedTasteInfoInput) {
//            bodyInfo to null
//        } else {
//            null to null
//        }
    }

    private val postUsersSignUpReq =
        combine(essentialInfo, nonEssentialInfo) { essentialInfo, nonEssentialInfo ->
            essentialInfo to nonEssentialInfo
        }.map { (essentialInfo, nonEssentialInfo) ->
            val (id, password, nickname) = essentialInfo
            val (bodyInfo, tasteInfo) = nonEssentialInfo

            PostUsersSignUpReq(
                username = id,
                password = password,
                nickname = nickname,
                age = bodyInfo.age?.toIntOrNull(),
                weight = bodyInfo.weight?.toFloatOrNull(),
                height = bodyInfo.height?.toFloatOrNull(),
                gender = "male",
                spicyPreference = tasteInfo.spicyPreference,
                meatConsumption = tasteInfo.meatConsumption,
                tastePreference = tasteInfo.tastePreference,
                activityLevel = tasteInfo.activityLevel,
                preferenceTypeFood = tasteInfo.preferenceTypeFood,
                preferenceFoods = "불고기"
            )
        }

    val isIdInvalid = _id.map { id ->
        !(id.length < 10 && isLettersOrDigits(id)) && id.isNotEmpty()
    }
    val isPasswordInvalid = _password.map { password ->
        password.length !in 6..15 && password.isNotEmpty()
    }

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

    private val isClickedRegisterButton = MutableStateFlow(false)

    fun onChangeId(value: String) {
        _id.value = value
    }

    fun onChangePassword(value: String) {
        _password.value = value
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

    fun onChangeGender(selectedIndex: Int) {
        _bodyInfo.value = bodyInfo.value.copy(
            gender = if (selectedIndex == 0)
                "male"
            else
                "female"
        )
    }

    fun onChangeSpicyPreference(value: Int) {
        tasteInfo.value = tasteInfo.value.copy(
            spicyPreference = value
        )
    }

    fun onChangeMeatConsumption(value: Boolean) {
        tasteInfo.value = tasteInfo.value.copy(
            meatConsumption = value
        )
    }

    fun onChangeTastePreference(value: String) {
        tasteInfo.value = tasteInfo.value.copy(
            tastePreference = value
        )
    }

    fun onChangeActivityLevel(value: Int) {
        tasteInfo.value = tasteInfo.value.copy(
            activityLevel = value
        )
        println("jaehoLee", "onChangeActivity = $value, ${tasteInfo.value.activityLevel}")
    }

    fun onChangePreferenceTypeFood(value: String) {
        tasteInfo.value = tasteInfo.value.copy(
            preferenceTypeFood = value
        )
    }

    fun onClickSkipTasteInfo() {
        tasteInfo.value = tasteInfo.value.copy(
            spicyPreference = null,
            meatConsumption = null,
            tastePreference = null,
            preferenceTypeFood = null
        )

        isClickedRegisterButton.value = true
    }

    fun onClickRegister() {
        isClickedRegisterButton.value = true
    }

    init {
        viewModelScope.launch {
            launch {
                combine(postUsersSignUpReq, isClickedRegisterButton) { req, isClicked ->
                    req to isClicked
                }.filter { (_, isClicked) ->
                    isClicked
                }.collectLatest { (req, _) ->
                    println("jaehoLee", "SignUpReq = $req")
                    ApiRequestHelper.makeRequest {
                        settingRepository.postUsersSignUp(req)
                    }.onSuccess { res ->
                        println("jaehoLee", "onSuccess: $res")
                        ApiRequestHelper.makeRequest {
                            settingRepository.postUsersLogin(
                                PostUsersLoginReq(
                                    username = req.username,
                                    password = req.password
                                )
                            )
                        }.onSuccess { loginRes ->
                            println("jaehoLee", "onSuccessLogin: $loginRes")
                            MainApplication.appPreference.isLogin = true
                            MainApplication.appPreference.userId = loginRes.result?.id ?: -1 // .toIntOrNull() ?: -1

                            _registerUiEvent.emit(RegisterUiEvent.OnFinishRegister)
                        }.onFailure { loginRes ->
                            println("jaehoLee", "onFailureLogin: $loginRes")
                            _registerUiEvent.emit(RegisterUiEvent.ShowFailedDialog)
                        }.onError { throwable ->
                            println("jaehoLee", "onErrorLogin: ${throwable.message}")
                            _registerUiEvent.emit(RegisterUiEvent.ShowFailedDialog)
                        }

                        isClickedRegisterButton.value = false
                    }.onFailure { res ->
                        isClickedRegisterButton.value = false

                        println("jaehoLee", "onFailure: $res")
                        when (res.code) {
                            301 -> _registerUiEvent.emit(RegisterUiEvent.UserInfoAlreadyExist)
                            else -> _registerUiEvent.emit(RegisterUiEvent.ShowFailedDialog)
                        }
                    }.onError { throwable ->
                        isClickedRegisterButton.value = false
                        println("jaehoLee", "onError: ${throwable.message}")
                    }
                }
            }
        }
    }

    sealed class RegisterUiEvent {
        data object OnFinishRegister : RegisterUiEvent()
        data object UserInfoAlreadyExist : RegisterUiEvent()
        data object ShowFailedDialog : RegisterUiEvent()
    }
}