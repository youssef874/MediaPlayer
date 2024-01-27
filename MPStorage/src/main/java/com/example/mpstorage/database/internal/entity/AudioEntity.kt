package com.example.mpstorage.database.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = AudioEntity.TABLE_NAME, indices = [Index(value = [AudioEntity.EXTERNAL_ID], unique = true)])
internal data class AudioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Long = 0,
    @ColumnInfo(ALBUM)
    val album: String = "",
    @ColumnInfo(URI)
    val uri: String = "",
    @ColumnInfo(SONG_NAME)
    val songName: String = "",
    @ColumnInfo(ARTIST)
    val artist: String = "",
    @ColumnInfo(DURATION)
    val duration: Int = -1,
    @ColumnInfo(SIZE)
    val size: Int = -1,
    @ColumnInfo(ALBUM_THUMBNAIL)
    val albumThumbnailUri: String = "",
    @ColumnInfo(IS_FAVORITE)
    val isFavorite: Boolean = true,
    @ColumnInfo(IS_INTERNAL)
    val isInternal: Boolean = true,
    @ColumnInfo(IS_OWNED)
    val isOwned: Boolean = true,
    @ColumnInfo(EXTERNAL_ID)
    val externalId: Long = 0
){

    companion object{
        const val TABLE_NAME = "audio"
        const val ID = "audio_id"
        const val ALBUM = "album"
        const val URI = "uri"
        const val SONG_NAME = "songName"
        const val ARTIST = "artist"
        const val DURATION = "duration"
        const val SIZE = "size"
        const val ALBUM_THUMBNAIL = "album_thumbnail_uri"
        const val IS_FAVORITE = "is_favorite"
        const val IS_INTERNAL = "is_internal"
        const val IS_OWNED = "is_owned"
        const val EXTERNAL_ID = "external_id"
    }
}
