package com.overeasy.smartfitness.scenario.setting.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.println
import com.overeasy.smartfitness.scenario.public.Header
import com.overeasy.smartfitness.scenario.setting.findid.FindIdScreen
import com.overeasy.smartfitness.scenario.setting.finish.FinishScreen
import com.overeasy.smartfitness.scenario.setting.finish.SettingFinishState
import com.overeasy.smartfitness.scenario.setting.login.LoginScreen
import com.overeasy.smartfitness.scenario.setting.logout.LogoutScreen
import com.overeasy.smartfitness.scenario.setting.myinfo.MyInfoScreen
import com.overeasy.smartfitness.scenario.setting.register.RegisterScreen
import com.overeasy.smartfitness.scenario.setting.register.RegisterState
import com.overeasy.smartfitness.scenario.setting.setting.SettingScreen
import com.overeasy.smartfitness.scenario.setting.withdraw.WithdrawScreen
import com.overeasy.smartfitness.ui.theme.ColorPrimary

@Composable
fun SettingNavHost(
    modifier: Modifier = Modifier
) {
    val navHostController = rememberNavController()

    var isLogin by remember { mutableStateOf(MainApplication.appPreference.isLogin) }
//    var isLogin by remember { mutableStateOf(false) }
    var finishState by remember { mutableStateOf(SettingFinishState.LoginFinish) }

    var currentDestination by remember { mutableStateOf(SettingRoutes.Setting.route) }
    val currentHeaderTitle by remember {
        derivedStateOf {
            when (currentDestination) {
                SettingRoutes.Setting.route -> "설정"
                SettingRoutes.Login.route -> "로그인"
                SettingRoutes.FindId.route -> "아이디 찾기"
                SettingRoutes.Register.route -> "회원가입"
                SettingRoutes.MyInfo.route -> "내 정보"
                SettingRoutes.Logout.route -> "로그아웃"
                SettingRoutes.Withdraw.route -> "탈퇴"
                SettingRoutes.Finish.route -> when (finishState) {
                    SettingFinishState.LoginFinish -> "로그인"
                    SettingFinishState.RegisterFinish -> "회원가입"
                    SettingFinishState.LogoutFinish -> "로그아웃"
                    SettingFinishState.WithdrawFinish -> "탈퇴"
                }

                else -> ""
            }
        }
    }

    var currentRegisterState by remember { mutableStateOf(RegisterState.UserInfoInput) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Header(
            title = currentHeaderTitle,
            isBackButtonEnabled = currentDestination != SettingRoutes.Setting.route,
            onClickBack = {
                if (currentDestination != SettingRoutes.Register.route) {
                    if (currentDestination != SettingRoutes.Finish.route) {
                        navHostController.navigateUp()
                    } else {
                        currentRegisterState = RegisterState.UserInfoInput
                        navHostController.popBackStack(
                            route = SettingRoutes.Setting.route,
                            inclusive = false
                        )
                    }
                } else {
                    when (currentRegisterState) {
                        RegisterState.UserInfoInput -> navHostController.navigateUp()
                        RegisterState.NicknameInput -> currentRegisterState =
                            RegisterState.UserInfoInput

                        RegisterState.BodyInfoInput -> currentRegisterState =
                            RegisterState.NicknameInput

                        RegisterState.TasteInfoInput -> currentRegisterState =
                            RegisterState.BodyInfoInput
                    }
                }
            }
//            onClickBack = navHostController::navigateUp
        )
        NavHost(
            navController = navHostController,
            startDestination = SettingRoutes.Setting.route
        ) {
            composable(SettingRoutes.Setting.route) {
                SettingScreen(
                    isLogin = isLogin,
                    onClickLogin = {
                        navHostController.navigate(SettingRoutes.Login.route)
                    },
                    onClickFindId = {
                        navHostController.navigate(SettingRoutes.FindId.route)
                    },
                    onClickRegister = {
                        navHostController.navigate(SettingRoutes.Register.route)
                    },
                    onClickMyInfo = {
                        navHostController.navigate(SettingRoutes.MyInfo.route)
                    },
                    onClickLogout = {
                        navHostController.navigate(SettingRoutes.Logout.route)
                    },
                    onClickWithdraw = {
                        navHostController.navigate(SettingRoutes.Withdraw.route)
                    }
                )
            }
            composable(SettingRoutes.Login.route) {
                LoginScreen(
                    onFinishLogin = {
                        isLogin = MainApplication.appPreference.isLogin
                        finishState = SettingFinishState.LoginFinish

                        navHostController.navigate(SettingRoutes.Finish.route)
                    }
                )
            }
            composable(SettingRoutes.FindId.route) {
                FindIdScreen(
                    onFinish = {
                        navHostController.navigateUp()
                    }
                )
            }
            composable(SettingRoutes.Register.route) {
                RegisterScreen(
                    registerState = currentRegisterState,
                    onChangeRegisterState = { registerState ->
                        currentRegisterState = registerState
                    },
                    onFinishRegister = {
                        isLogin = MainApplication.appPreference.isLogin
                        finishState = SettingFinishState.RegisterFinish

                        navHostController.navigate(SettingRoutes.Finish.route)
                    }
                )
            }
            composable(SettingRoutes.MyInfo.route) {
                MyInfoScreen()
            }
            composable(SettingRoutes.Logout.route) {
                LogoutScreen(
                    onFinishLogout = {
                        isLogin = MainApplication.appPreference.isLogin
                        finishState = SettingFinishState.LogoutFinish

                        navHostController.navigate(SettingRoutes.Finish.route)
                    }
                )
            }
            composable(SettingRoutes.Withdraw.route) {
                WithdrawScreen(
                    onFinishWithdraw = {
                        isLogin = MainApplication.appPreference.isLogin
                        finishState = SettingFinishState.WithdrawFinish

                        navHostController.navigate(SettingRoutes.Finish.route)
                    }
                )
            }
            composable(SettingRoutes.Finish.route) { backStackEntry ->
                FinishScreen(
                    finishState = finishState
                )
            }
        }
    }

    BackHandler {
        if (currentDestination != SettingRoutes.Register.route) {
            navHostController.navigateUp()
        } else {
            when (currentRegisterState) {
                RegisterState.UserInfoInput -> navHostController.navigateUp()
                RegisterState.NicknameInput -> currentRegisterState = RegisterState.UserInfoInput
                RegisterState.BodyInfoInput -> currentRegisterState = RegisterState.NicknameInput
                RegisterState.TasteInfoInput -> currentRegisterState = RegisterState.BodyInfoInput
            }
        }
    }

    LaunchedEffect(Unit) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination.route ?: SettingRoutes.Setting.route
        }
    }
}