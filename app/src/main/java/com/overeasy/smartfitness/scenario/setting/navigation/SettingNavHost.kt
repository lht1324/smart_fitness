package com.overeasy.smartfitness.scenario.setting.navigation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.scenario.public.Header
import com.overeasy.smartfitness.scenario.setting.login.LoginScreen
import com.overeasy.smartfitness.scenario.setting.logout.LogoutScreen
import com.overeasy.smartfitness.scenario.setting.myinfo.MyInfoScreen
import com.overeasy.smartfitness.scenario.setting.register.BodyInfoInputScreen
import com.overeasy.smartfitness.scenario.setting.register.RegisterScreen
import com.overeasy.smartfitness.scenario.setting.setting.SettingScreen
import com.overeasy.smartfitness.scenario.setting.withdraw.WithdrawScreen
import com.overeasy.smartfitness.showToast
import com.overeasy.smartfitness.ui.theme.ColorPrimary

@Composable
fun SettingNavHost(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val navHostController = rememberNavController()

    var currentDestination by remember { mutableStateOf(SettingRoutes.Setting.route) }
    val currentHeaderTitle by remember {
        derivedStateOf {
            when (currentDestination) {
                SettingRoutes.Setting.route -> "설정"
                SettingRoutes.MyInfo.route -> "회원가입"
                SettingRoutes.Login.route -> "로그인"
                SettingRoutes.Register.route -> "회원가입"
                SettingRoutes.Logout.route -> "로그아웃"
                SettingRoutes.Withdraw.route -> "탈퇴"
                else -> ""
            }
        }
    }

    var isLogin by remember { mutableStateOf(MainApplication.appPreference.isLogin) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = ColorPrimary)
    ) {
        Header(
            title = currentHeaderTitle,
            isBackButtonEnabled = currentDestination != SettingRoutes.Setting.route,
            onClickBack = navHostController::navigateUp
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
                    onFinishLogin = { msg ->
                        isLogin = MainApplication.appPreference.isLogin

                        navHostController.navigateUp()
                    }
                )
            }
            composable(SettingRoutes.Register.route) {
                RegisterScreen()
            }
            composable(SettingRoutes.MyInfo.route) {
                MyInfoScreen()
            }
            composable(SettingRoutes.Logout.route) {
                LogoutScreen()
            }
            composable(SettingRoutes.Withdraw.route) {
                WithdrawScreen()
            }
        }
    }

    LaunchedEffect(Unit) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination.route ?: SettingRoutes.Setting.route
        }
    }
}