package com.example.mpstorage.synchronizer.internal

import com.example.mpstorage.database.data.DBAudioData
import com.example.mpstorage.database.data.QueryAudio
import com.example.mpstorage.database.data.SearchAudio
import com.example.mpstorage.database.internal.IAudioDataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object FakeAudioDataProvider: IAudioDataProvider {

    private val list = mutableListOf<DBAudioData>()
    /*
    private val list = mutableListOf<DBAudioData>(
        DBAudioData(
            artist = "artist1",
            album = "album1",
            songName = "name1",
            externalId = 1L
        )
    )
    */
    private var notify: (suspend ()->Unit)? = null
    override suspend fun query(queryAudio: QueryAudio) {
        //TODO("Not yet implemented")
    }

    override fun query(query: SearchAudio): Flow<List<DBAudioData>> {
        return flow {

        }
    }

    override suspend fun add(data: DBAudioData) {
        //list.add(data)
        //notify?.invoke()
        throw IllegalArgumentException("test")
    }

    override suspend fun update(data: DBAudioData) {
        list.find { it.idAudio == data.idAudio }?.let {
            val index = list.indexOf(it)
            list[index] = data
        }?:run {
            list.add(data)
        }
        notify?.invoke()
    }

    override suspend fun delete(data: DBAudioData) {
        //TODO("Not yet implemented")
    }

    override fun observeAll(): Flow<List<DBAudioData>> {
        return flow {
            emit(list)
            notify = {
                emit(list)
            }
        }
    }

    override suspend fun getAll(): List<DBAudioData> {
        return list
    }

    override fun observeById(id: Long): Flow<DBAudioData?> {
        return flow {

        }
    }

    override suspend fun getById(id: Long): DBAudioData? {
        return list.find { it.externalId == id }
    }

    override suspend fun get(query: SearchAudio): DBAudioData? {
        return null
    }
}