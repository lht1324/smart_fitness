package com.overeasy.smartfitness.scenario.setting.register

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.domain.base.makeRequest
import com.overeasy.smartfitness.domain.foods.FoodsRepository
import com.overeasy.smartfitness.domain.setting.SettingRepository
import com.overeasy.smartfitness.domain.setting.dto.PostUsersLoginReq
import com.overeasy.smartfitness.domain.setting.dto.PostUsersSignUpReq
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val foodsRepository: FoodsRepository
) : ViewModel() {
    private val _registerUiEvent = MutableSharedFlow<RegisterUiEvent>()
    val registerUiEvent = _registerUiEvent.asSharedFlow()

    private val _id = MutableStateFlow("")
    val id = _id.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _nickname = MutableStateFlow("")
    val nickname = _nickname.asStateFlow()

    private val _bodyInfo = MutableStateFlow(
        RegisterBodyInfo(
            age = null,
            height = null,
            weight = null,
            gender = null
        )
    )
    val bodyInfo = _bodyInfo.asStateFlow()

    private val _tasteInfo = MutableStateFlow(
        RegisterTasteInfo(
            spicyPreference = null,
            meatConsumption = null,
            tastePreference = null,
            activityLevel = null,
            preferenceTypeFood = null,
            preferenceFoods = null
        )
    )
    val tasteInfo = _tasteInfo.asStateFlow()

    private val essentialInfo = combine(id, password, nickname, bodyInfo) { id, password, nickname, bodyInfo ->
        Triple(id, password, nickname) to bodyInfo
    }.filter { (_, bodyInfo) ->
        val (age, height, weight, gender) = bodyInfo

        !age.isNullOrEmpty() && !weight.isNullOrEmpty() && !height.isNullOrEmpty() && !gender.isNullOrEmpty()
    }

    private val nonEssentialInfo = tasteInfo.filter { tasteInfo ->
        val (_, _, _, activityLevel, _, preferenceFoods) = tasteInfo

        println("jaehoLee", "foods = $preferenceFoods")

        tasteInfo.activityLevel
        activityLevel != null && !preferenceFoods.isNullOrEmpty()
    }

    private val postUsersSignUpReq =
        combine(essentialInfo, nonEssentialInfo) { essentialInfo, nonEssentialInfo ->
            essentialInfo to nonEssentialInfo
        }.map { (essentialInfo, nonEssentialInfo) ->
            val (userInfo, bodyInfo) = essentialInfo

            val (id, password, nickname) = userInfo
            val tasteInfo = nonEssentialInfo

            PostUsersSignUpReq(
                username = id,
                password = password,
                nickname = nickname,
                age = bodyInfo.age!!.toInt(),
                weight = bodyInfo.weight!!.toFloat(),
                height = bodyInfo.height!!.toFloat(),
                gender = bodyInfo.gender!!,
                spicyPreference = tasteInfo.spicyPreference,
                meatConsumption = tasteInfo.meatConsumption,
                tastePreference = tasteInfo.tastePreference,
                activityLevel = tasteInfo.activityLevel!!,
                preferenceTypeFood = tasteInfo.preferenceTypeFood,
                preferenceFoods = tasteInfo.preferenceFoods
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

    private val _menuList = mutableStateListOf<String>()
    val menuList = _menuList

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
        _tasteInfo.value = _tasteInfo.value.copy(
            spicyPreference = value
        )
    }

    fun onChangeMeatConsumption(value: Boolean) {
        _tasteInfo.value = _tasteInfo.value.copy(
            meatConsumption = value
        )
    }

    fun onChangeTastePreference(value: String) {
        _tasteInfo.value = _tasteInfo.value.copy(
            tastePreference = value
        )
    }

    fun onChangeActivityLevel(value: Int) {
        _tasteInfo.value = _tasteInfo.value.copy(
            activityLevel = value
        )
    }

    fun onChangePreferenceTypeFood(value: String) {
        _tasteInfo.value = _tasteInfo.value.copy(
            preferenceTypeFood = value
        )
    }

    fun onChangePreferenceFoods(value: String) {
        _tasteInfo.value = _tasteInfo.value.copy(
            preferenceFoods = value
        )
    }

    fun onClickSkipTasteInfo() {
        _tasteInfo.value = _tasteInfo.value.copy(
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
            launch(Dispatchers.IO) {
                requestGetFoodsInit()
            }
            launch {
                combine(postUsersSignUpReq, isClickedRegisterButton) { req, isClicked ->
                    req to isClicked
                }.filter { (_, isClicked) ->
                    isClicked
                }.collectLatest { (req, _) ->
                    println("jaehoLee", "SignUpReq = $req")
                    requestPostUsersSignUp(req)
//                    ApiRequestHelper.makeRequest {
//                        settingRepository.postUsersSignUp(req)
//                    }.onSuccess { res ->
//                        println("jaehoLee", "onSuccess: $res")
//                        ApiRequestHelper.makeRequest {
//                            settingRepository.postUsersLogin(
//                                PostUsersLoginReq(
//                                    username = req.username,
//                                    password = req.password
//                                )
//                            )
//                        }.onSuccess { loginRes ->
//                            println("jaehoLee", "onSuccessLogin: $loginRes")
//                            MainApplication.appPreference.isLogin = true
//                            MainApplication.appPreference.userId = loginRes.result?.id ?: -1 // .toIntOrNull() ?: -1
//
//                            _registerUiEvent.emit(RegisterUiEvent.OnFinishRegister)
//                        }.onFailure { loginRes ->
//                            println("jaehoLee", "onFailureLogin: $loginRes")
//                            _registerUiEvent.emit(RegisterUiEvent.ShowFailedDialog)
//                        }.onError { throwable ->
//                            println("jaehoLee", "onErrorLogin: ${throwable.message}")
//                            _registerUiEvent.emit(RegisterUiEvent.ShowFailedDialog)
//                        }
//
//                        isClickedRegisterButton.value = false
//                    }.onFailure { res ->
//                        isClickedRegisterButton.value = false
//
//                        println("jaehoLee", "onFailure: $res")
//                        when (res.code) {
//                            301 -> _registerUiEvent.emit(RegisterUiEvent.UserInfoAlreadyExist)
//                            else -> _registerUiEvent.emit(RegisterUiEvent.ShowFailedDialog)
//                        }
//                    }.onError { throwable ->
//                        isClickedRegisterButton.value = false
//                        println("jaehoLee", "onError: ${throwable.message}")
//                    }
                }
            }
        }
    }

    private suspend fun requestPostUsersSignUp(req: PostUsersSignUpReq) {
        makeRequest {
            settingRepository.postUsersSignUp(req)
        }.onSuccess { res ->
            println("jaehoLee", "onSuccess: $res")

            requestPostUsersLogin(
                userName = req.username,
                password = req.password
            )
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

    private suspend fun requestPostUsersLogin(
        userName: String,
        password: String
    ) {
        makeRequest {
            settingRepository.postUsersLogin(
                PostUsersLoginReq(
                    username = userName,
                    password = password
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
    }

    private suspend fun requestGetFoodsInit() {
        makeRequest {
            foodsRepository.getFoodsInit()
        }.onSuccess { res ->
            val mappedList = res.result.map { menuData -> menuData.foodName }

            menuList.clear()
            menuList.addAll(mappedList)
        }.onFailure { res ->
            println("jaehoLee", "onFailure(requestGetFoodsInit()): ${res.message}") // 코드 없음
        }.onError { throwable ->
            println("jaehoLee", "onError(requestGetFoodsInit()): ${throwable.message}")
        }
    }

    sealed class RegisterUiEvent {
        data object OnFinishRegister : RegisterUiEvent()
        data object UserInfoAlreadyExist : RegisterUiEvent()
        data object ShowFailedDialog : RegisterUiEvent()
    }
}