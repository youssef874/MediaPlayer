package com.example.mediaplayer3.repository

import android.content.Context
import com.example.mediaplayer3.data.entity.MPAppAudio
import com.example.mplog.MPLogger
import com.example.mpstorage.database.DataBaseApi
import com.example.mpstorage.database.data.SearchAudio
import com.example.mpstorage.synchronizer.MPSynchroniseApi
import com.example.mpstorage.synchronizer.event.SynchronisationChanges
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class AudioDataRepo: IAudioDataRepo {

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
        awaitClose { eventCanceler.dispose() }
    }

    override fun getAllSong(context: Context): Flow<List<MPAppAudio>> {
        return DataBaseApi.forAudio(context).observeAll().map { list->
            list.map {
                it.toMPAppAudio()
            }
        }
    }

    override fun getById(context: Context, id: Long): Flow<MPAppAudio> {
        MPLogger.i(CLASS_NAME,"getById", TAG,"id: $id")
        return DataBaseApi.forAudio(context).getById(id).map { it.toMPAppAudio() }
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
}