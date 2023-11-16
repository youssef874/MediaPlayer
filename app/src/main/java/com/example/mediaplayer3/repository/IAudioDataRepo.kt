package com.example.mediaplayer3.repository

import android.content.Context
import com.example.mediaplayer3.data.entity.MPAppAudio
import com.example.mediaplayer3.data.entity.Result
import com.example.mpstorage.synchronizer.event.SynchronisationChanges
import com.example.mpstorage.synchronizer.event.SynchronisationType
import kotlinx.coroutines.flow.Flow

interface IAudioDataRepo {

    fun synchronization(context: Context)

    fun observeSynchronisationChanges(): Flow<SynchronisationChanges>

    fun getAllSong(context: Context): Flow<List<MPAppAudio>>

    fun getById(context: Context,id: Long): Flow<MPAppAudio>

    fun getSongsByAlbum(context: Context, album: String): Flow<List<MPAppAudio>>

    fun getSongsByArtist(context: Context, artist: String):Flow<List<MPAppAudio>>

    fun getSongsBySongName(context: Context, songName: String): Flow<List<MPAppAudio>>
}