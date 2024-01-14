package com.example.mpstorage.database.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "audio", indices = [Index(value = ["externalId"], unique = true)])
internal data class AudioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "audio_id")
    val id: Long = 0,
    @ColumnInfo("album")
    val album: String = "",
    @ColumnInfo("uri")
    val uri: String = "",
    @ColumnInfo("songName")
    val songName: String = "",
    @ColumnInfo("artist")
    val artist: String = "",
    @ColumnInfo("duration")
    val duration: Int = -1,
    @ColumnInfo("size")
    val size: Int = -1,
    @ColumnInfo("albumThumbnailUri")
    val albumThumbnailUri: String = "",
    @ColumnInfo("isFavorite")
    val isFavorite: Boolean = true,
    @ColumnInfo("isInternal")
    val isInternal: Boolean = true,
    @ColumnInfo("isOwned")
    val isOwned: Boolean = true,
    @ColumnInfo("externalId")
    val externalId: Long = 0
)
