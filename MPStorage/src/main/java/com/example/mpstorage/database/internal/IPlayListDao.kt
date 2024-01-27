package com.example.mpstorage.database.internal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mpstorage.database.internal.entity.AudioEntity
import com.example.mpstorage.database.internal.entity.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import com.example.mpstorage.database.internal.entity.SongWithPlaylists
import kotlinx.coroutines.flow.Flow

@Dao
internal interface IPlayListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlayList(playListEntity: PlayListEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef)

    @Update
    suspend fun updatePlayList(playListEntity: PlayListEntity)

    @Delete
    suspend fun deletePlayList(playListEntity: PlayListEntity)

    @Delete
    suspend fun deletePlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef)

    @Query("SELECT * FROM play_list")
    suspend fun getAllPlayList(): List<PlayListEntity>

    @Query("SELECT * FROM play_list")
    fun observeAll(): Flow<List<PlayListEntity>>

    @Transaction
    @Query("SELECT * FROM audio")
    suspend fun getListOfPlayListOfSongs(): List<SongWithPlaylists>

    @Transaction
    @Query("SELECT * FROM audio WHERE ${AudioEntity.ID} =:audioId")
    suspend fun getListOfPlayListForAudio(audioId: Long): SongWithPlaylists?

    @Transaction
    @Query("SELECT * FROM audio WHERE ${AudioEntity.ID} =:audioId")
    fun observeListOfPlayListForAudio(audioId: Long): Flow<SongWithPlaylists?>

    @Transaction
    @Query("SELECT * FROM audio")
    fun observeListOfPlayListOfSongs(): Flow<List<SongWithPlaylists>>

    @Query("SELECT * FROM play_list WHERE ${PlayListEntity.ID} =:id")
    suspend fun getPlayListById(id: Long): PlayListEntity?

    @Query("SELECT * FROM play_list WHERE ${PlayListEntity.ID} =:id")
    fun observePlayList(id: Long): Flow<PlayListEntity?>

    @Query("SELECT * FROM play_list WHERE ${PlayListEntity.NAME} =:name")
    fun observePlayListsByName(name:String): Flow<List<PlayListEntity>>
}