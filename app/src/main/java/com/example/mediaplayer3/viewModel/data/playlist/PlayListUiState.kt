package com.example.mediaplayer3.viewModel.data.playlist

import com.example.mediaplayer3.domain.entity.UiPlayList

data class PlayListUiState(
    val dataList: List<UiPlayList>? = null,
    val page: Int = 0,
    val isError: Boolean = false,
    val isNextItemLoading: Boolean = false,
    val isEndReached: Boolean = false,
    var isAudioAttachedToPlayList:Boolean = false
)
