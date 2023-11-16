package com.example.mediaplayer3.repository

import com.example.mediaplayer3.data.entity.MPAppAudio
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mpstorage.database.data.DBAudioData


fun DBAudioData.toMPAppAudio(): MPAppAudio{
    return MPAppAudio(
        id = idAudio, album = album, uri = uri, songName = songName, artist = artist,
        duration = duration, size = size, albumThumbnailUri = albumThumbnailUri, isFavorite = isFavorite, isInternal = isInternal,
        isOwned = isOwned
    )
}

fun MPAppAudio.toUiAudio(): UiAudio{
    return UiAudio(
        id = id, uri = uri, duration = duration, size = size,
        artistName = artist, album = album, songName = songName,
        albumThumbnailUri = albumThumbnailUri, isFavorite = isFavorite
    )
}