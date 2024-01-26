package com.example.mediaplayer3.viewModel.data.playlist

import android.content.Context

sealed class PlayListUiEvent {

    data class LoadData(val context: Context): PlayListUiEvent()

    data object LoadNextData: PlayListUiEvent()

    data class AttachSongToPlayList(val context: Context,val songId: Long?,val playListName: String):PlayListUiEvent()
}