package com.example.mpstorage.database.internal

import com.example.mplog.MPLogger
import com.example.mpstorage.database.data.DBAudioData
import com.example.mpstorage.database.data.QueryAudio
import com.example.mpstorage.database.data.SearchAudio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AudioDataProvider(private val audioDao: IAudioDao): IAudioDataProvider {


    override suspend fun query(queryAudio: QueryAudio) {
        MPLogger.d(CLASS_NAME,"query", TAG,"query: $queryAudio")
        queryAudio.toInternalQueryAudio(audioDao).action()
    }


    override suspend fun add(data: DBAudioData) {
        MPLogger.d(CLASS_NAME,"add", TAG,"dat: $data")
        audioDao.addAudio(data.toAudioEntity())
    }

    override suspend fun update(data: DBAudioData) {
        MPLogger.d(CLASS_NAME,"update", TAG,"data $data")
        audioDao.updateAudio(data.toAudioEntity())
    }

    override suspend fun delete(data: DBAudioData) {
        MPLogger.d(CLASS_NAME,"delete", TAG,"data: $data")
        audioDao.deleteAudio(data.toAudioEntity())
    }

    override fun observeAll(): Flow<List<DBAudioData>> {
        return audioDao.getAllAudio().map { it.map {item->item.toDBAudio() } }
    }

    override suspend fun getAll(): List<DBAudioData> {
        return audioDao.getAllAudios().map { it.toDBAudio() }
    }

    override fun getById(id: Long): Flow<DBAudioData> {
        MPLogger.d(CLASS_NAME,"getById", TAG,"id: $id")
        return audioDao.observeAudioById(id).map { it.toDBAudio() }
    }

    override fun query(query: SearchAudio): Flow<List<DBAudioData>> {
        MPLogger.d(CLASS_NAME,"query", TAG,"query: $query")
        return query.toBaseQueryAudio(audioDao).find()
    }

    companion object{
        private const val CLASS_NAME = "AudioDataProvider"
        private const val TAG = "DATA_BASE"
    }
}