package com.example.mpstorage.database.internal.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


internal data class PlaylistWithSongs(
    @Embedded val playListEntity: PlayListEntity,

    @Relation(
        parentColumn = "play_list_id",
        entityColumn = "audio_id",
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val songs: List<AudioEntity> = emptyList()
)

internal data class SongWithPlaylists(
    @Embedded val audioEntity: AudioEntity,

    @Relation(
        parentColumn = "audio_id",
        entityColumn = "play_list_id",
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val playLists: List<PlayListEntity> = emptyList()
)