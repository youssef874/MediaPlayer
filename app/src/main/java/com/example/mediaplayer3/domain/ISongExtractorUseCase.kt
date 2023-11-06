package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.data.entity.Result
import com.example.mediaplayer3.domain.entity.UiAudio

interface ISongExtractorUseCase {

    suspend fun getSongs(
        context: Context,
        filterByName: (()->String)? = null,
        filterByAlbum: (()->String)? = null,
        filterByArtist: (()->String)? = null,
    ): Result<List<UiAudio>>

    suspend fun getSong(context: Context, id: Long): Result<UiAudio?>

    fun getExtractedSongList(): List<UiAudio>
}