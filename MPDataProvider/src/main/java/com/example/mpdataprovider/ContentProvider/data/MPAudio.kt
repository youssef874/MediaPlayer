package com.example.mpdataprovider.ContentProvider.data

import android.net.Uri

data class MPAudio(
    var id: Long = -1,
    var uri: Uri = Uri.EMPTY,
    var duration: Int = -1,
    var size: Int = -1,
    var artistName: String = "",
    var album: String = "",
    var songName: String = "",
    var albumThumbnailUri: Uri? = null,
    var isFavorite: Boolean = false
)
