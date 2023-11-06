package com.example.mediaplayer3.viewModel.data

import com.example.mediaplayer3.domain.entity.UiAudio
import java.lang.Exception

data class TrackListUiState(
    val isLoading: Boolean = true,
    val isPermissionGranted: Boolean = false,
    val dataList: List<UiAudio> = emptyList(),
    val error: Exception? = null,
    val currentSelectedItem: UiAudio? = null,
    val needShowDialogForPermission: Boolean = false,
    val isPlaying: Boolean = false,
    val isInRandomMode: Boolean = false
)
