package com.example.mediaplayer3.repository

import android.content.Context
import android.net.Uri
import com.example.mediaplayer3.data.entity.Result
import com.example.mediaplayer3.ui.Constant
import com.example.mpdataprovider.contentprovider.data.MPAudio
import com.example.mpdataprovider.datadtore.RepeatMode
import kotlinx.coroutines.flow.Flow

interface IAudioRepository {

    suspend fun getAllSongs(context: Context): Result<List<MPAudio>>

    suspend fun getSongsByArtist(context: Context, artist: String): Result<List<MPAudio>>

    suspend fun getSongsBySongName(context: Context, songName: String): Result<List<MPAudio>>

    suspend fun getSongsByAlbum(context: Context, album: String): Result<List<MPAudio>>

    suspend fun getSongById(context: Context, id: Long): Result<MPAudio?>

    fun playSong(context: Context, uri: Uri,seekTo: Int = -1)

    fun stopSong(context: Context, uri: Uri)

    fun pauseSong(context: Context)

    fun resumeSong(context: Context,seekTo: Int = -1)

    fun forward(forwardTo: Int = Constant.Utils.DELTA_TIME)

    fun rewind(rewindTo: Int = Constant.Utils.DELTA_TIME)

    fun setPlayingPosition(context: Context,uri: Uri,position: Int)

    suspend fun updateLastPlayingSong(context: Context,value: Long)

    fun getLastPlayingSong(context: Context): Flow<Long>

    fun onSongCompletionListener(onSongCompleted: ()->Unit)

    fun songPlayingProgress(context: Context): Flow<Int>

    fun getLastPlayingSongDuration(context: Context): Flow<Int>

    fun changePlayNextOrPreviousMode(context: Context, isRandomMode: Boolean)

    fun getIsInRandomMode(context: Context): Flow<Boolean>

    suspend fun changeRepeatMode(context: Context,@RepeatMode repeatMode: Int)

    fun getRepeatMode(context: Context): Flow<@RepeatMode Int>
}