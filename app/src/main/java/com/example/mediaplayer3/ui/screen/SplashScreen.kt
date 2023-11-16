package com.example.mediaplayer3.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediaplayer3.ui.Constant
import com.example.mediaplayer3.ui.RequestPermissionDialog
import com.example.mediaplayer3.ui.RequestSinglePermission
import com.example.mediaplayer3.viewModel.SplashViewModel
import com.example.mediaplayer3.viewModel.data.splash.SplashUiEvent
import com.example.mplog.MPLogger


@Composable
fun SplashScreen(splashViewModel: SplashViewModel = viewModel()) {
    MPLogger.i(Constant.SplashScreen.CLASS_NAME,"SplashScreen",Constant.SplashScreen.TAG,"display splash screen")
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit){
        splashViewModel.onEvent(SplashUiEvent.Sync(context))
    }
    val state by splashViewModel.uiState.collectAsState()
    val permission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO
        else Manifest.permission.READ_EXTERNAL_STORAGE
    if (state.isLoading){
        MPLogger.i(Constant.SplashScreen.CLASS_NAME,"SplashScreen",Constant.SplashScreen.TAG,"screenIsLoading")
        LoadingScreen()
    }
    if (state.isSync){
        MPLogger.i(Constant.SplashScreen.CLASS_NAME,"SplashScreen",Constant.SplashScreen.TAG,"sync success")
        LoadingScreen()
        LaunchedEffect(key1 = Unit){
            splashViewModel.onEvent(SplashUiEvent.RequestData(context))
        }
    }
    if (state.isFailed){
        MPLogger.i(Constant.SplashScreen.CLASS_NAME,"SplashScreen",Constant.SplashScreen.TAG,"sync failed")
        if (ContextCompat.checkSelfPermission(
                context, permission
            ) != PackageManager.PERMISSION_GRANTED
        ){
            RequestSinglePermission(
                permission = permission,
                onPermissionGranted = {
                      LaunchedEffect(key1 = Unit){
                          splashViewModel.onEvent(SplashUiEvent.Sync(context))
                      }
                },
                onPermissionDenied = {
                    ErrorScreen()
                    RequestPermissionDialog(permissions = listOf(permission))
                }
            )
        }
    }
}