package com.example.mpstorage.database.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = PlayListEntity.TABLE_NAME)
internal data class PlayListEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(ID, defaultValue = "0")
    val id: Long = 0,
    @ColumnInfo(NAME, defaultValue = "")
    val name: String = ""
){
    companion object{
        const val TABLE_NAME = "play_list"
        const val ID = "play_list_id"
        const val NAME = "play_list_name"
    }
}
