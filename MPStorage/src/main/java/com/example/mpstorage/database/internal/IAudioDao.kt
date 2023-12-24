package com.example.mpstorage.database.internal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mpstorage.database.internal.entity.AudioEntity
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

    @Query("SELECT * FROM audio WHERE audio_id = :id")
    fun getAudioById(id: Long): Flow<AudioEntity>

    @Query("SELECT * FROM audio WHERE songName = :songName")
    fun getAudioBySongName(songName: String): Flow<List<AudioEntity>>

    @Query("SELECT * FROM audio WHERE album = :album")
    fun getAudioByAlbum(album: String): Flow<List<AudioEntity>>

    @Query("SELECT * FROM audio WHERE artist = :artist")
    fun getAudioArtist(artist: String): Flow<List<AudioEntity>>
}