package com.example.mpstorage.database.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_list")
internal data class PlayListEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("play_list_id", defaultValue = "0")
    val id: Long = 0,
    @ColumnInfo("play_list_name", defaultValue = "")
    val name: String = ""
)
