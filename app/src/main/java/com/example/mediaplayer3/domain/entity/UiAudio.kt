package com.example.mediaplayer3.domain.entity

import android.net.Uri

data class UiAudio(
    val id: Long = -1,
    val uri: Uri = Uri.EMPTY,
    val duration: Int = 0,
    val size: Int = -1,
    val artistName: String = "",
    val album: String = "",
    val songName: String = "",
    val albumThumbnailUri: Uri? = null,
    val isFavorite: Boolean = false
)
