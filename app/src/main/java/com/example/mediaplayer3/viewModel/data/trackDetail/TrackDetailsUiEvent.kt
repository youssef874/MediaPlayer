package com.example.mediaplayer3.viewModel.data.trackDetail

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.ui.Constant
import com.example.mediaplayer3.viewModel.data.UiEvent
import com.example.mpdataprovider.DataStore.RepeatMode

sealed class TrackDetailsUiEvent {

    data class SearchForCurrentSong(val songId: Long, val context: Context): TrackDetailsUiEvent()

    data class PauseOrResume(
        val uiAudio: UiAudio,
        val context: Context
    ): TrackDetailsUiEvent()

    data class PlayNextSong(val context: Context): TrackDetailsUiEvent()

    data class PlayPreviousSong(val context: Context): TrackDetailsUiEvent()

    data class Rewind(val rewindTo: Int = Constant.Utils.DELTA_TIME, val context: Context) : TrackDetailsUiEvent()

    data class Forward(val forwardTo: Int = Constant.Utils.DELTA_TIME, val context: Context): TrackDetailsUiEvent()

    data class UpdatePlayingPosition(val context: Context,val position: Int): TrackDetailsUiEvent()

    data class ChangePlayNextBehavior(val context: Context): TrackDetailsUiEvent()

    data class ChangeRepeatMode(val context: Context): TrackDetailsUiEvent()

    data class ChangeFavoriteStatus(val context: Context): TrackDetailsUiEvent()
}