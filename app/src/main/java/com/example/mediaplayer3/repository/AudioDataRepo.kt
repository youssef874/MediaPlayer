package com.example.mediaplayer3.repository

import android.content.Context
import android.net.Uri
import com.example.mediaplayer3.data.entity.MPAppAudio
import com.example.mediaplayer3.data.entity.MPAppPlayList
import com.example.mediaplayer3.data.entity.RepeatMode
import com.example.mpcore.api.data.datastore.MPDataStoreApi
import com.example.mpcore.api.log.MPLog
import com.example.mpmediamanager.MpAudioManagerApi
import com.example.mpstorage.database.DataBaseApi
import com.example.mpstorage.database.data.PlayListQuery
import com.example.mpstorage.database.data.QueryAudio
import com.example.mpstorage.database.data.SearchAudio
import com.example.mpstorage.synchronizer.MPSynchroniseApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AudioDataRepo @Inject constructor() : IAudioDataRepo {

    companion object {
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
        return DataBaseApi.forAudio(context).observeAll().map { list ->
            list.map {
                it.toMPAppAudio()
            }
        }
    }

    override fun getAllPlayListAsFlow(context: Context): Flow<List<MPAppPlayList>> {
        return DataBaseApi.forPlayList(context).observeAll()
            .map { list -> list.map { it.toMPAppPlayList() } }
    }

    override fun getById(context: Context, id: Long): Flow<MPAppAudio?> {
        MPLog.i(CLASS_NAME, "getById", TAG, "id: $id")
        return DataBaseApi.forAudio(context).observeById(id).map { it?.toMPAppAudio() }
    }

    override fun getSongsByAlbum(context: Context, album: String): Flow<List<MPAppAudio>> {
        MPLog.i(CLASS_NAME, "getSongsByAlbum", TAG, "album: $album")
        return DataBaseApi.forAudio(context)
            .query(SearchAudio.SearchByAlbum(album))
            .map { value -> value.map { it.toMPAppAudio() } }
    }

    override fun getSongsByArtist(context: Context, artist: String): Flow<List<MPAppAudio>> {
        MPLog.i(CLASS_NAME, "getSongsByArtist", TAG, "artist: $artist")
        return DataBaseApi.forAudio(context)
            .query(SearchAudio.SearchByArtist(artist))
            .map { value -> value.map { it.toMPAppAudio() } }
    }

    override fun getSongsBySongName(context: Context, songName: String): Flow<List<MPAppAudio>> {
        MPLog.i(CLASS_NAME, "getSongsBySongName", TAG, "songName: $songName")
        return DataBaseApi.forAudio(context)
            .query(SearchAudio.SearchBySongName(songName))
            .map { value -> value.map { it.toMPAppAudio() } }
    }

    override suspend fun changeIsFavoriteStatusToSong(
        context: Context,
        songId: Long,
        isFavorite: Boolean
    ) {
        MPLog.d(
            CLASS_NAME,
            "changeIsFavoriteStatusToSong",
            TAG,
            "songId: $songId, isFavorite: $isFavorite"
        )
        DataBaseApi.forAudio(context).query(QueryAudio.ChaneIsFavorite(songId, isFavorite))
    }

    override suspend fun attachPlaylistToSong(
        context: Context,
        songId: Long,
        mpAppPlayList: MPAppPlayList
    ) {
        DataBaseApi.forPlayList(context).query(PlayListQuery.AddSongToPlayList(mpAppPlayList.toDBPlayList(),songId))
    }

    override suspend fun addPlayList(context: Context, mpAppPlayList: MPAppPlayList) {
        DataBaseApi.forPlayList(context).add(mpAppPlayList.toDBPlayList())
    }

    override fun observeSongListByPlayListId(
        context: Context,
        playListId: Long
    ): Flow<List<MPAppAudio>> {
        return DataBaseApi.forAudio(context).query(SearchAudio.SearchForAllSongForPlayList(playListId)).map { list->list.map { it.toMPAppAudio() } }
    }

    override suspend fun getFirstPlaylistSong(context: Context, playListId: Long): MPAppAudio? {
        MPLog.d(CLASS_NAME,"getFirstPlaylistSong", TAG,"playListId: $playListId")
        return DataBaseApi.forAudio(context).get(SearchAudio.GetFirstSongInPlaylist(playListId))?.toMPAppAudio()
    }

    override fun playSong(context: Context, uri: Uri, playAt: Int) {
        MPLog.d(CLASS_NAME, "playSong", TAG, "uri: $uri, playAt: $playAt")
        MpAudioManagerApi.playSong(context, uri, playAt)
    }

    override fun resumeSong(context: Context, seekTo: Int) {
        MPLog.d(CLASS_NAME, "resumeSong", TAG, "seekTo: $seekTo")
        MpAudioManagerApi.resumeSong(context, seekTo)
    }

    override fun stopSong(context: Context, uri: Uri) {
        MPLog.d(CLASS_NAME, "stopSong", TAG, "uri: $uri")
        MpAudioManagerApi.stopSong(context, uri)
    }

    override fun pauseSong(context: Context) {
        MPLog.d(CLASS_NAME, "pauseSong", TAG, "pause current playing song")
        MpAudioManagerApi.pauseSong(context)
    }

    override fun observeSongCompletion(
        onComplete: () -> Unit
    ) {
        MpAudioManagerApi.observeSongCompletion(onComplete)
    }

    override fun observeSongProgression() = MpAudioManagerApi.observeDurationProgress()

    override suspend fun updateLastPlayingSong(context: Context, songId: Long) {
        MPLog.i(CLASS_NAME, "updateLastPlayingSong", TAG, "songId: $songId")
        MPDataStoreApi.lastPlayingSong(context).updateValue(songId)
    }

    override fun observeLastPlayingSong(context: Context): Flow<Long> {
        return MPDataStoreApi.lastPlayingSong(context).observeValue()
    }

    override suspend fun updateLastSongProgress(context: Context, progress: Int) {
        MPLog.d(CLASS_NAME, "updateLastSongProgress", TAG, "progress: $progress")
        MPDataStoreApi.lastPlayingSongLastDuration(context).updateValue(progress)
    }

    override fun observeLastSongProgression(context: Context): Flow<Int> {
        return MPDataStoreApi.lastPlayingSongLastDuration(context).observeValue()
    }

    override suspend fun updateRandomMode(context: Context, isRandom: Boolean) {
        MPLog.d(CLASS_NAME, "updateRandomMode", TAG, "isRandom: $isRandom")
        MPDataStoreApi.isInRandomMode(context).updateValue(isRandom)
    }

    override fun observeIsRandomMode(context: Context): Flow<Boolean> {
        return MPDataStoreApi.isInRandomMode(context).observeValue()
    }

    override suspend fun updateRepeatMode(context: Context, repeatMode: RepeatMode) {
        MPDataStoreApi.repeatMode(context).updateValue(repeatMode.toAnnotation())
    }

    override fun observeRepeatMode(context: Context): Flow<RepeatMode> {
        return MPDataStoreApi.repeatMode(context).observeValue().map { it.toEnum() }
    }

    override suspend fun setPlayingPosition(context: Context, uri: Uri, position: Int) {
        MPLog.d(CLASS_NAME, "setPlayingPosition", TAG, "uri: $uri, position: $position")
        MpAudioManagerApi.setThePositionToPlayWith(context, uri, position)
        MPDataStoreApi.lastPlayingSongLastDuration(context).updateValue(position)
    }

    override fun forward(forwardTo: Int) {
        MPLog.d(CLASS_NAME, "forward", TAG, "forwardTo: $forwardTo")
        MpAudioManagerApi.forward(forwardWith = forwardTo)
    }

    override fun rewind(rewindTo: Int) {
        MPLog.d(CLASS_NAME, "rewind", TAG, "forwardTo: $rewindTo")
        MpAudioManagerApi.rewind(rewindWith = rewindTo)
    }
}