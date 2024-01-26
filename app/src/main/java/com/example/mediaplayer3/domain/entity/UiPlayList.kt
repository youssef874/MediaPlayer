package com.example.mediaplayer3.domain.entity

import android.net.Uri

data class UiPlayList(
    val playListId: Long = 0,
    val playListName: String = "",
    val thumbnailUri: Uri? = null
)
