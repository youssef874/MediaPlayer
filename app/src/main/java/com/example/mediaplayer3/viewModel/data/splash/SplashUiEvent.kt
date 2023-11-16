package com.example.mediaplayer3.viewModel.data.splash

import android.content.Context

sealed class SplashUiEvent {

    data class Sync(val context: Context): SplashUiEvent()

    data class RequestData(val context: Context): SplashUiEvent()
}