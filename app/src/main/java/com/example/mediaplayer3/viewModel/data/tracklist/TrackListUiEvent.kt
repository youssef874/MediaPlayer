package com.example.mediaplayer3.viewModel.data.tracklist

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio

sealed class TrackListUiEvent {

    data class LoadData(val context: Context): TrackListUiEvent()

    data object LoadNextData: TrackListUiEvent()

    data class ClickSong(val uiAudio: UiAudio,val context: Context): TrackListUiEvent()

    data class PlayOrPause(val context: Context): TrackListUiEvent()

    data class PlayNextSong(val context: Context): TrackListUiEvent()

    data class PlayPreviousSong(val context: Context): TrackListUiEvent()

}