package com.example.mpdataprovider.contentprovider.internal

import android.content.Context
import com.example.mpdataprovider.contentprovider.data.MPAudio

internal interface IAudioProvider {

    suspend fun getAllSongs(context: Context): List<MPAudio>

    suspend fun getAllSongByArtist(context: Context,artistName: String): List<MPAudio>

    suspend fun getAllSongsByAlbum(context: Context,album: String): List<MPAudio>

    suspend fun getAllSongsBySongName(context: Context, songName: String): List<MPAudio>

    suspend fun getSongById(context: Context,id: Long): MPAudio?

    fun setOnDataChangesListener(onDataChanges: () -> Unit)
}