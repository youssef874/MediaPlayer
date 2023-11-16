package com.example.mediaplayer3.viewModel.data.splash

data class SplashUiState(
    val isLoading: Boolean = true,
    val isFailed: Boolean = false,
    val isSuccess: Boolean = false,
    val isSync: Boolean = false
)
