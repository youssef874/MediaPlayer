package com.example.mpstorage.database.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity( primaryKeys = [PlayListEntity.ID,AudioEntity.ID])
data class PlaylistSongCrossRef(
    @ColumnInfo(name = PlayListEntity.ID, defaultValue = "0")
    val playListId: Long = 0L,
    @ColumnInfo(name = AudioEntity.ID, defaultValue = "0")
    val songId: Long = 0L
)
