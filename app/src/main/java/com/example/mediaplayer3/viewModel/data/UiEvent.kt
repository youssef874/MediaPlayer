package com.example.mediaplayer3.viewModel.data

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.ui.Constant

sealed class UiEvent {

    data class FetchData(val context: Context) : UiEvent()

    data object NotifyPermissionNeeded : UiEvent()

    data class PlaySong(val uiAudio: UiAudio, val context: Context, val playAt: Int = -1) :
        UiEvent()

    data class PauseOrResume(
        val uiAudio: UiAudio,
        val context: Context
    ) : UiEvent()

    data class PlayNextSong(val context: Context) : UiEvent()

    data class PlayPreviousSong(val context: Context) : UiEvent()


}