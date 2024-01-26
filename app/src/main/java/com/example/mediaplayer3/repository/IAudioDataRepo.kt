package com.example.mediaplayer3.repository

import android.content.Context
import android.net.Uri
import com.example.mediaplayer3.data.entity.MPAppAudio
import com.example.mediaplayer3.data.entity.MPAppPlayList
import com.example.mediaplayer3.data.entity.RepeatMode
import com.example.mpstorage.synchronizer.event.SynchronisationChanges
import kotlinx.coroutines.flow.Flow

interface IAudioDataRepo {

    fun synchronization(context: Context)

    fun observeSynchronisationChanges(): Flow<SynchronisationChanges>

    fun getAllSong(context: Context): Flow<List<MPAppAudio>>

    fun getAllPlayListAsFlow(context: Context): Flow<List<MPAppPlayList>>

    fun getById(context: Context,id: Long): Flow<MPAppAudio?>

    fun getSongsByAlbum(context: Context, album: String): Flow<List<MPAppAudio>>

    fun getSongsByArtist(context: Context, artist: String):Flow<List<MPAppAudio>>

    fun getSongsBySongName(context: Context, songName: String): Flow<List<MPAppAudio>>

    suspend fun changeIsFavoriteStatusToSong(context: Context,songId: Long,isFavorite: Boolean)

    suspend fun attachPlaylistToSong(context: Context,songId: Long,mpAppPlayList: MPAppPlayList)

    suspend fun addPlayList(context: Context,mpAppPlayList: MPAppPlayList)

    fun observeSongListByPlayListId(context: Context,playListId: Long): Flow<List<MPAppAudio>>

    suspend fun getFirstPlaylistSong(context: Context, playListId: Long): MPAppAudio?

    fun playSong(context: Context,uri: Uri,playAt: Int = -1)

    fun resumeSong(context: Context, seekTo: Int)

    fun stopSong(context: Context, uri: Uri)

    fun pauseSong(context: Context)

    fun observeSongCompletion(onComplete: ()->Unit)

    fun observeSongProgression(): Flow<Int>

    suspend fun updateLastPlayingSong(context: Context,songId: Long)

    fun observeLastPlayingSong(context: Context): Flow<Long>

    suspend fun updateLastSongProgress(context: Context, progress: Int)

    fun observeLastSongProgression(context: Context): Flow<Int>

    suspend fun updateRandomMode(context: Context, isRandom: Boolean)

    fun observeIsRandomMode(context: Context): Flow<Boolean>

    suspend fun updateRepeatMode(context: Context, repeatMode: RepeatMode)

    fun observeRepeatMode(context: Context): Flow<RepeatMode>

    suspend fun setPlayingPosition(context: Context, uri: Uri, position: Int)

    fun forward(forwardTo: Int)

    fun rewind(rewindTo: Int)
}