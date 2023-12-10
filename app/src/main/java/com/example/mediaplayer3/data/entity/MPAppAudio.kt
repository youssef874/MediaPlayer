package com.example.mediaplayer3.data.entity

import android.net.Uri

data class MPAppAudio(
    val id: Long = 0,
    val album: String = "",
    val uri: Uri = Uri.EMPTY,
    val songName: String = "",
    val artist: String = "",
    val duration: Int = -1,
    val size: Int = -1,
    val albumThumbnailUri: Uri? = null,
    val isFavorite: Boolean = false,
    val isInternal: Boolean = true,
    val isOwned: Boolean = true
)
