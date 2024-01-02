package com.example.mpstorage.database.internal

import com.example.mpstorage.database.data.DBAudioData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal sealed interface Query


internal sealed class BaseAudioQuery(protected val audioDao: IAudioDao): Query {

    abstract fun find(): Flow<List<DBAudioData>>

    class FindBySongNameQueryBase(audioDao: IAudioDao, private val songName: String): BaseAudioQuery(audioDao){
        override fun find(): Flow<List<DBAudioData>> {
            return super.audioDao.getAudioBySongName(songName).map { it.map { item->item.toDBAudio() } }
        }

    }

    class FindBayAlbumQueryBase(audioDao: IAudioDao, private val album: String): BaseAudioQuery(audioDao) {
        override fun find(): Flow<List<DBAudioData>> {
            return super.audioDao.getAudioByAlbum(album).map { it.map { item->item.toDBAudio() } }
        }
    }

    class FindByArtistNameObject(audioDao: IAudioDao, private val artist: String): BaseAudioQuery(audioDao) {
        override fun find(): Flow<List<DBAudioData>> {
            return super.audioDao.getAudioArtist(artist).map { it.map { item->item.toDBAudio() } }
        }
    }
}

internal sealed class InternalAudioQuery(protected val audioDao: IAudioDao): Query{

    abstract suspend fun action()

    class ChangeIsFavorite(audioDao: IAudioDao,private val songId: Long, private val isFavorite: Boolean):InternalAudioQuery(audioDao){
        override suspend fun action() {
            val audio = super.audioDao.getAudioById(songId)
            val updatedAudio = audio?.copy(isFavorite = isFavorite)
            if (updatedAudio != null) {
                super.audioDao.updateAudio(updatedAudio)
            }
        }

    }
}


