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
import com.example.mpstorage.database.internal.entity.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
internal interface IAudioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAudio(audioEntity: AudioEntity)

    @Update
    suspend fun updateAudio(audioEntity: AudioEntity)

    @Delete
    suspend fun deleteAudio(audioEntity: AudioEntity)

    @Query("SELECT * from audio ORDER BY songName ASC")
    fun getAllAudio(): Flow<List<AudioEntity>>

    @Query("SELECT * from audio ORDER BY songName ASC")
    suspend fun getAllAudios(): List<AudioEntity>

    @Query("SELECT * FROM audio WHERE ${AudioEntity.ID} = :id")
    fun observeAudioById(id: Long): Flow<AudioEntity?>

    @Query("SELECT * FROM audio WHERE ${AudioEntity.ID} = :id")
    suspend fun getAudioById(id: Long): AudioEntity?

    @Query("SELECT * FROM audio WHERE ${AudioEntity.SONG_NAME} = :songName")
    fun getAudioBySongName(songName: String): Flow<List<AudioEntity>>

    @Query("SELECT * FROM audio WHERE ${AudioEntity.ALBUM} = :album")
    fun getAudioByAlbum(album: String): Flow<List<AudioEntity>>

    @Query("SELECT * FROM audio WHERE ${AudioEntity.ARTIST} = :artist")
    fun getAudioArtist(artist: String): Flow<List<AudioEntity>>

    @Transaction
    @Query("SELECT * FROM play_list")
    suspend fun getListOfAudioListOfPlayList(): List<PlaylistWithSongs>

    @Transaction
    @Query("SELECT * FROM play_list WHERE ${PlayListEntity.ID} =:playListId")
    suspend fun getListOfAudioForPlayList(playListId: Long): PlaylistWithSongs?

    @Transaction
    @Query("SELECT * FROM play_list WHERE ${PlayListEntity.ID} =:playListId")
    fun observeListOfAudioForPlayList(playListId: Long): Flow<PlaylistWithSongs?>

    @Transaction
    @Query("SELECT * FROM play_list")
    fun observeListOfAudioListOfPlayList(): Flow<List<PlaylistWithSongs>>
}