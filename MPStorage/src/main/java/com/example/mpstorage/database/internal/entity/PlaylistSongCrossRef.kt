package com.example.mpstorage.database.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity( primaryKeys = ["play_list_id","audio_id"])
data class PlaylistSongCrossRef(
    @ColumnInfo(name = "play_list_id", defaultValue = "0")
    val playListId: Long = 0L,
    @ColumnInfo(name = "audio_id", defaultValue = "0")
    val songId: Long = 0L
)
