package com.example.mediaplayer3.repository

import android.content.Context
import android.net.Uri
import com.example.mediaplayer3.data.entity.MPAppAudio
import com.example.mediaplayer3.data.entity.RepeatMode
import com.example.mpdataprovider.datastore.AudioDataStoreApi
import com.example.mplog.MPLogger
import com.example.mpmediamanager.MpAudioManagerApi
import com.example.mpstorage.database.DataBaseApi
import com.example.mpstorage.database.data.QueryAudio
import com.example.mpstorage.database.data.SearchAudio
import com.example.mpstorage.synchronizer.MPSynchroniseApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AudioDataRepo @Inject constructor() : IAudioDataRepo {

    companion object{
        private const val CLASS_NAME = "AudioDataRepo"
        private const val TAG = "AUDIO"
    }

    override fun synchronization(context: Context) {
        MPSynchroniseApi.synchronize(context)
    }

    override fun observeSynchronisationChanges() = callbackFlow {
        val eventCanceler = MPSynchroniseApi.subscribeToSynchronizationChanges {
            trySend(it)
        }
        awaitClose {
            eventCanceler.dispose()
        }
    }

    override fun getAllSong(context: Context): Flow<List<MPAppAudio>> {
        return DataBaseApi.forAudio(context).observeAll().map { list->
            list.map {
                it.toMPAppAudio()
            }
        }
    }

    override fun getById(context: Context, id: Long): Flow<MPAppAudio?> {
        MPLogger.i(CLASS_NAME,"getById", TAG,"id: $id")
        return DataBaseApi.forAudio(context).observeById(id).map { it?.toMPAppAudio() }
    }

    override fun getSongsByAlbum(context: Context, album: String): Flow<List<MPAppAudio>> {
        MPLogger.i(CLASS_NAME,"getSongsByAlbum", TAG,"album: $album")
        return DataBaseApi.forAudio(context)
            .query(SearchAudio.SearchByAlbum(album)).map { value -> value.map { it.toMPAppAudio() } }
    }

    override fun getSongsByArtist(context: Context, artist: String): Flow<List<MPAppAudio>> {
        MPLogger.i(CLASS_NAME,"getSongsByArtist", TAG,"artist: $artist")
        return DataBaseApi.forAudio(context)
            .query(SearchAudio.SearchByArtist(artist)).map { value -> value.map { it.toMPAppAudio() } }
    }

    override fun getSongsBySongName(context: Context, songName: String): Flow<List<MPAppAudio>> {
        MPLogger.i(CLASS_NAME,"getSongsBySongName", TAG,"songName: $songName")
        return DataBaseApi.forAudio(context)
            .query(SearchAudio.SearchBySongName(songName)).map { value -> value.map { it.toMPAppAudio() } }
    }

    override suspend fun changeIsFavoriteStatusToSong(
        context: Context,
        songId: Long,
        isFavorite: Boolean
    ) {
        MPLogger.d(CLASS_NAME,"changeIsFavoriteStatusToSong", TAG,"songId: $songId, isFavorite: $isFavorite")
        DataBaseApi.forAudio(context).query(QueryAudio.ChaneIsFavorite(songId, isFavorite))
    }

    override fun playSong(context: Context, uri: Uri, playAt: Int) {
        MPLogger.d(CLASS_NAME,"playSong", TAG,"uri: $uri, playAt: $playAt")
        MpAudioManagerApi.playSong(context, uri,playAt)
    }

    override fun resumeSong(context: Context, seekTo: Int) {
        MPLogger.d(CLASS_NAME,"resumeSong", TAG,"seekTo: $seekTo")
        MpAudioManagerApi.resumeSong(context, seekTo)
    }

    override fun stopSong(context: Context, uri: Uri) {
        MPLogger.d(CLASS_NAME,"stopSong", TAG,"uri: $uri")
        MpAudioManagerApi.stopSong(context, uri)
    }

    override fun pauseSong(context: Context) {
        MPLogger.d(CLASS_NAME,"pauseSong", TAG,"pause current playing song")
        MpAudioManagerApi.pauseSong(context)
    }

    override fun observeSongCompletion(
        onComplete: () -> Unit
    ) {
        MpAudioManagerApi.observeSongCompletion(onComplete)
    }

    override fun observeSongProgression() = MpAudioManagerApi.observeDurationProgress()

    override suspend fun updateLastPlayingSong(context: Context, songId: Long) {
        MPLogger.i(CLASS_NAME,"updateLastPlayingSong", TAG,"songId: $songId")
        AudioDataStoreApi.lastPlayingSong(context).updateValue(songId)
    }

    override fun observeLastPlayingSong(context: Context): Flow<Long> {
        return AudioDataStoreApi.lastPlayingSong(context).getValue()
    }

    override suspend fun updateLastSongProgress(context: Context, progress: Int) {
        MPLogger.d(CLASS_NAME,"updateLastSongProgress", TAG,"progress: $progress")
        AudioDataStoreApi.lastPlayingSongLastDuration(context).updateValue(progress)
    }

    override fun observeLastSongProgression(context: Context): Flow<Int> {
        return AudioDataStoreApi.lastPlayingSongLastDuration(context).getValue()
    }

    override suspend fun updateRandomMode(context: Context, isRandom: Boolean) {
        MPLogger.d(CLASS_NAME,"updateRandomMode", TAG,"isRandom: $isRandom")
        AudioDataStoreApi.isInRandomMode(context).updateValue(isRandom)
    }

    override fun observeIsRandomMode(context: Context): Flow<Boolean> {
        return AudioDataStoreApi.isInRandomMode(context).getValue()
    }

    override suspend fun updateRepeatMode(context: Context, repeatMode: RepeatMode) {
        AudioDataStoreApi.repeatMode(context).updateValue(repeatMode.toAnnotation())
    }

    override fun observeRepeatMode(context: Context): Flow<RepeatMode> {
        return AudioDataStoreApi.repeatMode(context).getValue().map { it.toEnum() }
    }

    override suspend fun setPlayingPosition(context: Context, uri: Uri, position: Int) {
        MPLogger.d(CLASS_NAME,"setPlayingPosition", TAG,"uri: $uri, position: $position")
        MpAudioManagerApi.setThePositionToPlayWith(context, uri, position)
        AudioDataStoreApi.lastPlayingSongLastDuration(context).updateValue(position)
    }

    override fun forward(forwardTo: Int) {
        MPLogger.d(CLASS_NAME,"forward", TAG,"forwardTo: $forwardTo")
        MpAudioManagerApi.forward(forwardWith = forwardTo)
    }

    override fun rewind(rewindTo: Int) {
        MPLogger.d(CLASS_NAME,"rewind", TAG,"forwardTo: $rewindTo")
        MpAudioManagerApi.rewind(rewindWith = rewindTo)
    }
}