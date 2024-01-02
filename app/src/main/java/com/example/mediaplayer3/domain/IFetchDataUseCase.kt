package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.data.entity.Result
import com.example.mediaplayer3.domain.entity.UiAudio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface IFetchDataUseCase {

    fun requestData(
        context: Context,
        filterByName: (()->String)? = null,
        filterByAlbum: (()->String)? = null,
        filterByArtist: (()->String)? = null,
    ):Flow<List<UiAudio>>

    fun getSong(context: Context, id: Long): Flow<UiAudio?>

    fun getExtractedSongList(): List<UiAudio>

    fun getSongListByPage(page: Int, pageSize: Int): Result<List<UiAudio>>

    fun observeLastPlayingSongId(context: Context): SharedFlow<Long>
}