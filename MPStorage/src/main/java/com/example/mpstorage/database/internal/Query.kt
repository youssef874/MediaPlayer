package com.example.mpstorage.database.internal

import com.example.mpstorage.database.data.DBAudioData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal sealed interface Query

internal sealed class AudioQuery(private val audioDao: IAudioDao): Query {

    abstract fun find(): Flow<List<DBAudioData>>

    class FindBySongNameQuery(audioDao: IAudioDao, private val songName: String): AudioQuery(audioDao){
        override fun find(): Flow<List<DBAudioData>> {
            return super.audioDao.getAudioBySongName(songName).map { it.map { item->item.toDBAudio() } }
        }

    }

    class FindBayAlbumQuery(audioDao: IAudioDao, private val album: String): AudioQuery(audioDao) {
        override fun find(): Flow<List<DBAudioData>> {
            return super.audioDao.getAudioByAlbum(album).map { it.map { item->item.toDBAudio() } }
        }
    }

    class FindByArtistNameObject(audioDao: IAudioDao, private val artist: String): AudioQuery(audioDao) {
        override fun find(): Flow<List<DBAudioData>> {
            return super.audioDao.getAudioArtist(artist).map { it.map { item->item.toDBAudio() } }
        }
    }
}

