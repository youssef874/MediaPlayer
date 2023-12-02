package com.example.mediaplayer3.viewModel.data.tracklist

import com.example.mediaplayer3.domain.entity.UiAudio

data class TrackListUiState(
    val isLoading: Boolean = true,
    val iNextItemsLoading: Boolean = false,
    val isError: Boolean = false,
    val dataList: List<UiAudio> = emptyList(),
    val page: Int = 0,
    val isEndReached: Boolean = false,
    val currentSelectedItem: UiAudio? = null,
    val isPlaying: Boolean = false,
)
