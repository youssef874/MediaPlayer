package com.example.mediaplayer3.repository

import android.content.Context
import android.net.Uri
import com.example.mediaplayer3.data.entity.Result
import com.example.mpdataprovider.contentprovider.AudioApi
import com.example.mpdataprovider.contentprovider.data.MPAudio
import com.example.mpdataprovider.datastore.AudioDataStoreApi
import com.example.mplog.MPLogger
import com.example.mpmediamanager.MpAudioManagerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class AudioRepositoryImpl(private val coroutineScope: CoroutineScope): IAudioRepository {

    override suspend fun getAllSongs(context: Context): Result<List<MPAudio>> {
        return try {
            MPLogger.i(CLASS_NAME,"getAllSongs", TAG,"get all songs")
            val result = AudioApi.getAllSongs(context)
            Result.Success(result)
        }catch (e: Exception){
            MPLogger.e(CLASS_NAME,"getAllSongs", TAG,"error: ${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun getSongsByArtist(context: Context, artist: String): Result<List<MPAudio>> {
        return try {
            MPLogger.i(CLASS_NAME,"getSongsByArtist", TAG,"get all songs artist: $artist")
            val result = AudioApi.getSongsByArtist(context,artist)
            Result.Success(result)
        }catch (e: Exception){
            MPLogger.e(CLASS_NAME,"getSongsByArtist", TAG,"error: ${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun getSongsBySongName(
        context: Context,
        songName: String
    ): Result<List<MPAudio>> {
        return try {
            MPLogger.i(CLASS_NAME,"getSongsBySongName", TAG,"get all songs songName: $songName")
            val result = AudioApi.getSongsBySongName(context,songName)
            Result.Success(result)
        }catch (e: Exception){
            MPLogger.e(CLASS_NAME,"getSongsBySongName", TAG,"error: ${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun getSongsByAlbum(context: Context, album: String): Result<List<MPAudio>> {
        return try {
            MPLogger.i(CLASS_NAME,"getSongsByAlbum", TAG,"get all songs album: $album")
            val result = AudioApi.getSongsByAlbum(context,album)
            Result.Success(result)
        }catch (e: Exception){
            MPLogger.e(CLASS_NAME,"getSongsByAlbum", TAG,"error: ${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun getSongById(context: Context, id: Long): Result<MPAudio?> {
        return try {
            MPLogger.d(CLASS_NAME,"getSongById", TAG,"get all songs id: $id")
            val result = AudioApi.getSongById(context,id)
            Result.Success(result)
        }catch (e: Exception){
            MPLogger.e(CLASS_NAME,"getSongById", TAG,"error: ${e.message}")
            Result.Error(e)
        }
    }

    override fun playSong(context: Context, uri: Uri, seekTo: Int) {
        MPLogger.d(CLASS_NAME,"playSong", TAG,"play song: $uri")
        MpAudioManagerApi.playSong(context, uri,seekTo)
    }

    override fun stopSong(context: Context, uri: Uri) {
        MPLogger.d(CLASS_NAME,"stopSong", TAG,"stop song: $uri")
        MpAudioManagerApi.stopSong(context, uri)
    }

    override fun pauseSong(context: Context) {
        MPLogger.d(CLASS_NAME,"pauseSong", TAG,"pause")
        MpAudioManagerApi.pauseSong(context)
    }

    override fun resumeSong(context: Context, seekTo: Int) {
        MPLogger.d(CLASS_NAME,"resumeSong", TAG,"resume")
        MpAudioManagerApi.resumeSong(context,seekTo)
    }

    override fun forward(forwardTo: Int) {
        MPLogger.d(CLASS_NAME,"forward", TAG,"forwardTo: $forwardTo")
        MpAudioManagerApi.forward(forwardTo)
    }

    override fun rewind(rewindTo: Int) {
        MPLogger.d(CLASS_NAME,"rewind", TAG,"rewindTo: $rewindTo")
        MpAudioManagerApi.rewind(rewindTo)
    }

    override fun setPlayingPosition(context: Context, uri: Uri, position: Int) {
        MPLogger.d(CLASS_NAME,"setPlayingPosition", TAG,"uri: $uri, position: $position")
        MpAudioManagerApi.setThePositionToPlayWith(context, uri, position)
        coroutineScope.launch {
            AudioDataStoreApi.lastPlayingSongLastDuration(context).updateValue(position)
        }
    }

    override suspend fun updateLastPlayingSong(context: Context, value: Long) {
        AudioDataStoreApi.lastPlayingSong(context).updateValue(value)
    }

    override fun getLastPlayingSong(context: Context): Flow<Long> {
        return AudioDataStoreApi.lastPlayingSong(context).getValue()
    }

    override fun onSongCompletionListener(onSongCompleted: () -> Unit) {

    }

    override fun songPlayingProgress(context: Context) = callbackFlow<Int> {

    }

    override fun getLastPlayingSongDuration(context: Context): Flow<Int> {
        return AudioDataStoreApi.lastPlayingSongLastDuration(context).getValue()
    }

    override fun changePlayNextOrPreviousMode(context: Context, isRandomMode: Boolean) {
        MPLogger.d(CLASS_NAME,"changePlayNextMode", TAG,"isRandomMode: $isRandomMode")
        coroutineScope.launch {
            AudioDataStoreApi.isInRandomMode(context).updateValue(isRandomMode)
        }
    }

    override fun getIsInRandomMode(context: Context): Flow<Boolean> {
        return AudioDataStoreApi.isInRandomMode(context).getValue()
    }

    override suspend fun changeRepeatMode(context: Context, repeatMode: Int) {
        MPLogger.d(CLASS_NAME,"changeRepeatMode", TAG,"repeatMode: $repeatMode")
        AudioDataStoreApi.repeatMode(context).updateValue(repeatMode)
    }

    override fun getRepeatMode(context: Context): Flow<Int> {
        return AudioDataStoreApi.repeatMode(context).getValue()
    }

    companion object{
        private const val CLASS_NAME = "AudioRepositoryImpl"
        private const val TAG = "AUDIO"
    }
}