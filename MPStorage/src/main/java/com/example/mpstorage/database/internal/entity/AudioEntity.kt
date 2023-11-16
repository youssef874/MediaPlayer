package com.example.mpstorage.database.internal.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "audio", indices = [Index(value = ["externalId"], unique = true)])
data class AudioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "audio_id")
    val id: Long = 0,
    val album: String = "",
    val uri: String = "",
    val songName: String = "",
    val artist: String = "",
    val duration: Int = -1,
    val size: Int = -1,
    val albumThumbnailUri: String = "",
    val isFavorite: Boolean = true,
    val isInternal: Boolean = true,
    val isOwned: Boolean = true,
    val externalId: Long = 0
)
