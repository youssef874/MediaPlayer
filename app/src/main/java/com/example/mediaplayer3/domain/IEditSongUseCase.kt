package com.example.mediaplayer3.domain

import android.content.Context

interface IEditSongUseCase {

    suspend fun changeIsFavoriteStatus(context: Context, songId: Long,isFavorite: Boolean)
}