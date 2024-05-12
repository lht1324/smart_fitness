@file:OptIn(ExperimentalPermissionsApi::class)

package com.overeasy.smartfitness

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.overeasy.smartfitness.appConfig.MainApplication
import com.overeasy.smartfitness.model.ScreenState
import com.overeasy.smartfitness.scenario.diary.navigation.DiaryNavHost
import com.overeasy.smartfitness.scenario.diet.navigation.DietNavHost
import com.overeasy.smartfitness.scenario.public.Dialog
import com.overeasy.smartfitness.scenario.ranking.navigation.RankingNavHost
import com.overeasy.smartfitness.scenario.setting.navigation.SettingNavHost
import com.overeasy.smartfitness.scenario.workout.navigation.WorkoutNavHost
import com.overeasy.smartfitness.ui.theme.ColorPrimary
import com.overeasy.smartfitness.ui.theme.ColorSecondary
import com.overeasy.smartfitness.ui.theme.SmartFitnessTheme
import com.overeasy.smartfitness.ui.theme.fontFamily
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.util.date.getTimeMillis
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.time.LocalDateTime

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val jsonList = mutableStateListOf<String>()

    private val WRITE_REQUEST_CODE: Int = 43

    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/json"
            putExtra(Intent.EXTRA_TITLE, LocalDateTime.now().run { "${String.format("%02d", year)}/${String.format("%02d", monthValue + 1)}/${String.format("%02d", dayOfMonth)}_${String.format("%02d", hour)}:${String.format("%02d", minute)}:${String.format("%02d", second)}:${String.format("%02d", getTimeMillis())}.json" })
        }
        startActivityForResult(intent, WRITE_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WRITE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> data?.data?.also {
                    writeInFile(
                        it,
                        jsonList.joinToString { json ->
                            "$json\n\n"
                        }.replace("\n\n,{", "\n{")
                    )
                }
                Activity.RESULT_CANCELED -> {
                }
            }
        }
    }


    private fun writeInFile(uri: Uri, text: String) {
        try {
            contentResolver.openOutputStream(uri)?.also {
                println("jaehoLee", "text = $text")
                val bw = BufferedWriter(OutputStreamWriter(it))
                bw.write(text)
                bw.flush()
                bw.close()
                jsonList.clear()
            } ?: println("jaehoLee", "uri is null")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isSystemInDarkTheme = isSystemInDarkTheme()
            var isShowRequestPermissionDialog by remember { mutableStateOf(false) }
            var isShowFinishDialog by remember { mutableStateOf(false) }

            var isWorkoutRunning by remember { mutableStateOf(false) }

            val cameraPermissionState = rememberPermissionState(
                permission = Manifest.permission.CAMERA,
                onPermissionResult = { _ ->
                    MainApplication.appPreference.isAlreadyRequestedCameraPermission = true
                    isShowRequestPermissionDialog = false
                }
            )

            val pagerState = rememberPagerState(
                pageCount = {
                    ScreenState.entries.size
                }
            )

            val tabItemList = ScreenState.entries.map { state ->
                state.value
            }
            var currentPage by remember { mutableIntStateOf(2) }
//            var currentPage by remember { mutableIntStateOf(3) }
            var headerHeight by remember { mutableIntStateOf(0) }

            val screenHeight = LocalConfiguration.current.screenHeightDp
            var tabHeight by remember { mutableFloatStateOf(0f) }

            SmartFitnessTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    CurrentScreen(
                        modifier = Modifier.height(((screenHeight - tabHeight).dp)),
                        stateValue = tabItemList[currentPage],
                        onUpdateJson = { json ->
                            jsonList.add(json)
                        },
                        onChangeIsWorkoutRunning = { isRunning ->
                            isWorkoutRunning = isRunning
                        },
                        onChangeHeaderHeight = {
                            if (headerHeight != it) {
                                headerHeight = it
                            }
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.dp,
                        color = ColorSecondary
                    )
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = ColorPrimary,
                        contentColor = ColorSecondary,
                        indicator = { _ ->
                            SecondaryIndicator( height = 1.dp, color = Color.Transparent)
                        }
                    ) {
                        tabItemList.forEachIndexed { index, tabItem ->
                            Row {
                                Tab(
                                    selected = currentPage == index,
                                    onClick = {
                                        if (!isWorkoutRunning)
                                            currentPage = index

//                                        if (currentPage == 3) {
//                                            createFile()
//                                        }
                                    },
                                    modifier = Modifier
                                        .border(
                                            width = 0.5.dp,
                                            color = ColorSecondary
                                        )
                                        .onSizeChanged { (_, height) ->
                                            val heightDp = pxToDp(height)

                                            if (heightDp != tabHeight) {
                                                tabHeight = heightDp
                                            }
                                        },
                                    text = {
                                        Text(
                                            text = tabItem,
                                            fontSize = 12.dpToSp(),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = fontFamily
                                        )
                                    },
                                    selectedContentColor = Color.White,
                                    unselectedContentColor = ColorSecondary
                                )
                            }
                        }
                    }
                }
            }

            BackHandler {
                isShowFinishDialog = !isShowFinishDialog
            }

            if (isShowRequestPermissionDialog) {
                Dialog(
                    title = "카메라 권한을 허용해야 운동을 분석할 수 있어요.",
                    description = "권한을 거부한 뒤 허용하고 싶다면\n" +
                            "'앱 설정 -> 권한'에서 카메라 권한을 허용해 주세요.",
                    confirmText = "취소",
                    dismissText = "허용하기",
                    onClickConfirm = {
                        isShowRequestPermissionDialog = false
                    },
                    onClickDismiss = {
                        MainApplication.appPreference.isAlreadyRequestedCameraPermission = true
                        cameraPermissionState.launchPermissionRequest()
                    },
                    onDismissRequest = {
                        isShowRequestPermissionDialog = false
                    }
                )
            }

            if (isShowFinishDialog && !isWorkoutRunning) {
                Dialog(
//                    title = "끄려고요?",
//                    description = "운동은 하고 가는 거죠?",
                    title = "운동은 하고 가는 거죠?",
                    description = "켰다 끄는 건 좀 아니긴 한데...\n뭐라 하려는 건 아니에요 ㅋㅋ",
                    confirmText = "아니",
                    dismissText = "응",
                    onClickConfirm = {
                        isShowFinishDialog = false
                    },
                    onClickDismiss = {
                        finish()
                    },
                    onDismissRequest = {
                        isShowFinishDialog = false
                    }
                )
            }

            LaunchedEffect(Unit) {
                enableEdgeToEdge(
                    statusBarStyle = if (isSystemInDarkTheme) {
                        SystemBarStyle.dark(ColorPrimary.toArgb())
                    } else {
                        SystemBarStyle.light(ColorPrimary.toArgb(), ColorPrimary.toArgb())
                    }
                )

                if (cameraPermissionState.status.shouldShowRationale) {
                    MainApplication.appPreference.isAlreadyRequestedCameraPermission = false
                }

                isShowRequestPermissionDialog = !(MainApplication.appPreference.isAlreadyRequestedCameraPermission)
            }
        }
    }
}

@Composable
fun CurrentScreen(
    modifier: Modifier = Modifier,
    stateValue: String,
    onUpdateJson: (String) -> Unit,
    onChangeIsWorkoutRunning: (Boolean) -> Unit,
    onChangeHeaderHeight: (Int) -> Unit
) = Box(
    modifier = modifier
) {
    when (stateValue) {
        ScreenState.DietScreen.value -> DietNavHost()
        ScreenState.DiaryScreen.value -> DiaryNavHost()
        ScreenState.MainScreen.value -> WorkoutNavHost(
            onUpdateJson = onUpdateJson,
            onChangeIsWorkoutRunning = onChangeIsWorkoutRunning,
            onChangeHeaderHeight = onChangeHeaderHeight
        )
        ScreenState.RankingScreen.value -> RankingNavHost()
        ScreenState.SettingScreen.value -> SettingNavHost()
        else -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = ColorPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "화면 로딩에 실패했습니다.",
                color = Color.White,
                fontSize = 18.dpToSp(),
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}