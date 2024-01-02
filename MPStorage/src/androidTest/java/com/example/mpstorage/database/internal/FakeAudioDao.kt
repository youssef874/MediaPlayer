package com.example.mpstorage.database.internal

import com.example.mpstorage.database.internal.entity.AudioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object FakeAudioDao: IAudioDao {

    private val list = mutableListOf<AudioEntity>()
    private var notify: (suspend ()->Unit)? = null

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
}