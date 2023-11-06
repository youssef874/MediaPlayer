package com.example.mediaplayer3.viewModel.data.trackDetail

import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mpdataprovider.DataStore.RepeatMode

data class TrackDetailUiState (
    val currentSong: UiAudio = UiAudio() ,
    val songProgress: Int = -1,
    val isPlaying: Boolean = false,
    val isInRandomMode: Boolean = false,
    val repeatMode:@RepeatMode Int = RepeatMode.NO_REPEAT
)