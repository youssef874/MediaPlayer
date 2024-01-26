package com.example.mpstorage.database.internal

import com.example.mpstorage.database.internal.entity.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import com.example.mpstorage.database.internal.entity.SongWithPlaylists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal object FakePlayListDao: IPlayListDao {

    private val list = mutableListOf<PlayListEntity>()
    private var notify: (suspend ()->Unit)? = null
    private val listCross = mutableListOf<PlaylistSongCrossRef>()
    private var notifyCross: (suspend ()->Unit)? = null

   private var listWith = mutableListOf<SongWithPlaylists>()

    override suspend fun addPlayList(playListEntity: PlayListEntity): Long {
        list.add(playListEntity)
        notify?.invoke()
        return playListEntity.id
    }

    override suspend fun addPlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef) {
        val audio = FakeAudioDao.getAudioById(playlistSongCrossRef.songId)
        audio?.let { SongWithPlaylists(it, list) }?.let {
            listWith.add(
                it
            )
        }
        FakeAudioDao.addWith(playlistSongCrossRef)
        listCross.add(playlistSongCrossRef)
        notifyCross?.invoke()
        notify?.invoke()
    }

    override suspend fun updatePlayList(playListEntity: PlayListEntity) {
        list.find { it.id == playListEntity.id }?.let {
            val index = list.indexOf(it)
            list[index] = playListEntity
        }?:run {
            list.add(playListEntity)
        }
        notify?.invoke()
    }

    override suspend fun deletePlayList(playListEntity: PlayListEntity) {
       list.find { it.id == playListEntity.id }?.let {
           list.remove(it)
       }
        notify?.invoke()
    }

    override suspend fun deletePlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef) {
        listCross.remove(playlistSongCrossRef)
        listWith.find { it.audioEntity.id ==playlistSongCrossRef.songId }?.let {
            listWith.remove(it)
        }
        FakeAudioDao.removeWith(playlistSongCrossRef)
        notifyCross?.invoke()
        notify?.invoke()
    }

    override suspend fun getAllPlayList(): List<PlayListEntity> {
        return list
    }

    override fun observeAll(): Flow<List<PlayListEntity>> {
        return flow {
            emit(list)
            notify = {
                emit(list)
            }
        }
    }

    override suspend fun getListOfPlayListOfSongs(): List<SongWithPlaylists> {
        return listWith
    }

    override suspend fun getListOfPlayListForAudio(audioId: Long): SongWithPlaylists? {
        return listWith.find { it.audioEntity.id == audioId }
    }

    override fun observeListOfPlayListForAudio(audioId: Long): Flow<SongWithPlaylists?> {
        return flow {
            emit(listWith.find { it.audioEntity.id == audioId })
            notify = {
                emit(listWith.find { it.audioEntity.id == audioId })
            }
            notifyCross = {
                emit(listWith.find { it.audioEntity.id == audioId })
            }
        }
    }

    override fun observeListOfPlayListOfSongs(): Flow<List<SongWithPlaylists>> {
       return flow {
           emit(listWith)
           notify = {
               emit(listWith)
           }
           notify = {
               emit(listWith)
           }
       }
    }

    override suspend fun getPlayListById(id: Long): PlayListEntity? {
        return list.find { it.id == id }
    }

    override fun observePlayList(id: Long): Flow<PlayListEntity?> {
        return flow {
            emit(list.find { it.id == id })
            notify = {
                emit(list.find { it.id == id })
            }
        }
    }

    override fun observePlayListsByName(name: String): Flow<List<PlayListEntity>> {
        return flow {
            emit(list.filter { it.name == name })
        }
    }
}