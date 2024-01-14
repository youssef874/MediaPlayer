package com.example.mpstorage.database.internal

import com.example.mpstorage.database.internal.entity.AudioEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import com.example.mpstorage.database.internal.entity.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal object FakeAudioDao: IAudioDao {

    private val list = mutableListOf<AudioEntity>()
    private var notify: (suspend ()->Unit)? = null

    private val listWith = mutableListOf<PlaylistWithSongs>()

    suspend fun addWith(playlistSongCrossRef: PlaylistSongCrossRef){
        val playlist = FakePlayListDao.getPlayListById(playlistSongCrossRef.playListId)
        playlist?.let {
            listWith.add(PlaylistWithSongs(it, list))
        }
        notify?.invoke()
    }

    suspend fun removeWith(playlistSongCrossRef: PlaylistSongCrossRef){
        listWith.find { it.playListEntity.id == playlistSongCrossRef.playListId }?.let {
            listWith.remove(it)
        }
        notify?.invoke()
    }

    override suspend fun addAudio(audioEntity: AudioEntity) {
        list.add(audioEntity)
        notify?.invoke()
    }

    override suspend fun updateAudio(audioEntity: AudioEntity) {
       list.find { it.id == audioEntity.id }?.let {
           val index = list.indexOf(it)
           list[index] = audioEntity
       }?:run {
           list.add(audioEntity)
       }
        notify?.invoke()
    }

    override suspend fun deleteAudio(audioEntity: AudioEntity) {
        list.find { it.id == audioEntity.id }?.let {
            list.remove(it)
        }
        notify?.invoke()
    }

    override fun getAllAudio(): Flow<List<AudioEntity>> {
        return flow {
            emit(list)
            notify = {
                emit(list)
            }
        }
    }

    override suspend fun getAllAudios(): List<AudioEntity> {
        return list
    }

    override fun observeAudioById(id: Long): Flow<AudioEntity?> {
       return flow {
           list.find { it.id == id }?.let {
               emit(it)
           }
           notify = {
               list.find { it.id == id }?.let {
                   emit(it)
               }
           }
       }
    }

    override suspend fun getAudioById(id: Long): AudioEntity? {
        return list.find { it.id == id }
    }

    override fun getAudioBySongName(songName: String): Flow<List<AudioEntity>> {
        return flow {
            emit(list.filter { it.songName == songName })
            notify = {
                emit(list.filter { it.songName == songName })
            }
        }
    }

    override fun getAudioByAlbum(album: String): Flow<List<AudioEntity>> {
        return flow {
            emit(list.filter { it.album == album })
            notify = {
                emit(list.filter { it.album == album })
            }
        }
    }

    override fun getAudioArtist(artist: String): Flow<List<AudioEntity>> {
        return flow {
            emit(list.filter { it.artist == artist })
            notify = {
                emit(list.filter { it.artist == artist })
            }
        }
    }

    override suspend fun getListOfAudioListOfPlayList(): List<PlaylistWithSongs> {
        return listWith
    }

    override suspend fun getListOfAudioForPlayList(playListId: Long): PlaylistWithSongs? {
        return listWith.find { it.playListEntity.id == playListId }
    }

    override fun observeListOfAudioForPlayList(playListId: Long): Flow<PlaylistWithSongs?> {
        return flow {
            emit(listWith.find { it.playListEntity.id == playListId })
            notify = {
                emit(listWith.find { it.playListEntity.id == playListId })
            }
        }
    }

    override fun observeListOfAudioListOfPlayList(): Flow<List<PlaylistWithSongs>> {
        return flow {
            emit(listWith)
            notify = {
                emit(listWith)
            }
        }
    }
}