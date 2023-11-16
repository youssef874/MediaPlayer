package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.data.entity.MPAppAudio
import com.example.mediaplayer3.domain.entity.UiAudio
import kotlinx.coroutines.flow.Flow

interface IFetchDataUseCase {

    fun requestData(
        context: Context,
        filterByName: (()->String)? = null,
        filterByAlbum: (()->String)? = null,
        filterByArtist: (()->String)? = null,
    ):Flow<List<UiAudio>>

    fun getSong(context: Context, id: Long): Flow<UiAudio>

    fun getExtractedSongList(): List<UiAudio>
}