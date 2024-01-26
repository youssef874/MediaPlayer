package com.example.mpstorage.database.internal

import com.example.mplog.MPLogger
import com.example.mpstorage.database.data.DBAudioData
import com.example.mpstorage.database.data.DBPlayListData
import com.example.mpstorage.database.internal.entity.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import com.example.mpstorage.database.internal.entity.PlaylistWithSongs
import com.example.mpstorage.database.internal.entity.SongWithPlaylists
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

// FIXME nee refactor

internal sealed interface Query


internal sealed class BaseAudioQuery(protected val audioDao: IAudioDao) : Query {

    abstract fun find(): Flow<List<DBAudioData>>

    class FindBySongNameQueryBase(audioDao: IAudioDao, private val songName: String) :
        BaseAudioQuery(audioDao) {
        override fun find(): Flow<List<DBAudioData>> {
            return super.audioDao.getAudioBySongName(songName)
                .map { it.map { item -> item.toDBAudio() } }
        }

    }

    class FindBayAlbumQueryBase(audioDao: IAudioDao, private val album: String) :
        BaseAudioQuery(audioDao) {
        override fun find(): Flow<List<DBAudioData>> {
            return super.audioDao.getAudioByAlbum(album).map { it.map { item -> item.toDBAudio() } }
        }
    }

    class FindByArtistNameObject(audioDao: IAudioDao, private val artist: String) :
        BaseAudioQuery(audioDao) {
        override fun find(): Flow<List<DBAudioData>> {
            return super.audioDao.getAudioArtist(artist).map { it.map { item -> item.toDBAudio() } }
        }
    }

    class FindAllSongForPlayList(audioDao: IAudioDao, private val playListId: Long) :
        BaseAudioQuery(audioDao) {
        override fun find(): Flow<List<DBAudioData>> {
            return audioDao.observeListOfAudioForPlayList(playListId)
                .map { value: PlaylistWithSongs? ->
                    value?.songs?.map { it.toDBAudio() } ?: emptyList()
                }
        }
    }

    class FindFirstPlaylistAudio( audioDao: IAudioDao,private val playListId: Long): BaseAudioQuery(audioDao), IAudioOneShotFinder {

        override suspend fun oneShotFinder(): DBAudioData? {
            return withContext(Dispatchers.IO){
                audioDao.getListOfAudioForPlayList(playListId)?.songs?.first()?.toDBAudio()
            }
        }

        override fun find(): Flow<List<DBAudioData>> {
            return flow {
                //Do nothing
            }
        }
    }
}

internal sealed interface IOneShotFinder<T>{

    suspend fun oneShotFinder(): T
}

internal sealed interface IAudioOneShotFinder: IOneShotFinder<DBAudioData?>

internal sealed class InternalPlayListFinder(protected val playListDao: IPlayListDao) : Query {

    abstract fun observe(): Flow<List<DBPlayListData>>

    class FindPlayListByName(playListDao: IPlayListDao, private val playListName: String) :
        InternalPlayListFinder(playListDao) {
        override fun observe(): Flow<List<DBPlayListData>> {
            return playListDao.observePlayListsByName(playListName)
                .map { value: List<PlayListEntity> -> value.map { it.toDBPlayListData() } }
        }
    }

    class FindAllPlayListForAudio(playListDao: IPlayListDao, private val audioId: Long) :
        InternalPlayListFinder(playListDao) {

        override fun observe(): Flow<List<DBPlayListData>> {
            return playListDao.observeListOfPlayListForAudio(audioId)
                .map { value: SongWithPlaylists? ->
                    value?.playLists?.map { it.toDBPlayListData() } ?: emptyList()
                }
        }
    }
}

internal sealed class InternalAudioQuery(protected val audioDao: IAudioDao) : Query {

    abstract suspend fun action()

    class ChangeIsFavorite(
        audioDao: IAudioDao,
        private val songId: Long,
        private val isFavorite: Boolean
    ) : InternalAudioQuery(audioDao) {
        override suspend fun action() {
            withContext(Dispatchers.IO){
                val audio = super.audioDao.getAudioById(songId)
                val updatedAudio = audio?.copy(isFavorite = isFavorite)
                if (updatedAudio != null) {
                    super.audioDao.updateAudio(updatedAudio)
                }
            }
        }

    }
}

internal sealed class InternalPlayListQuery(protected val playListDao: IPlayListDao) : Query {


    abstract suspend fun action()

    class AttachAudioToPlayList(
        playListDao: IPlayListDao,
        private val audioId: Long,
        private val playList: DBPlayListData
    ) : InternalPlayListQuery(playListDao) {

        companion object{
            private const val CLASS_NAME = "AttachAudioToPlayList"
            private const val TAG = "PLAY_LIST_QUERY"
        }
        override suspend fun action() {
            withContext(Dispatchers.IO){
                val playListEntity = playListDao.getPlayListById(playList.playListId)
                var playListId = playList.playListId
                var deferred: Deferred<Long>? = null
                if (playListEntity == null) {
                    MPLogger.d(CLASS_NAME,"action", TAG,"${playList.playListId} not existed in database add it first")
                    deferred = async { playListDao.addPlayList(playList.toPlayListEntity()) }
                }
                MPLogger.d(CLASS_NAME,"action", TAG,"attach songId: $audioId, with playList with id: $playListId")
                deferred?.let { playListId = it.await() }
                playListDao.addPlaylistSongCrossRef(
                    PlaylistSongCrossRef(playListId = playListId, songId = audioId)
                )
            }
        }
    }
}


