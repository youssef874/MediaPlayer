package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiPlayList

interface IEditSongUseCase {

    suspend fun changeIsFavoriteStatus(context: Context, songId: Long,isFavorite: Boolean)

    suspend fun attachSongToPlayList(context: Context,songId: Long, playList: UiPlayList)
}