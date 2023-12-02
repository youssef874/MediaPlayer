package com.example.mediaplayer3.viewModel.data.splash

import android.content.Context

sealed class SplashUiEvent {

    data class Sync(val context: Context): SplashUiEvent()
}