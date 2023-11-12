package com.example.mpstorage.database.internal

import com.example.mpstorage.database.data.DBAudioData
import com.example.mpstorage.database.data.SearchAudio
import com.example.mpstorage.database.internal.entity.AudioEntity
import com.example.mpstorage.internal.entity.IAudioDao


internal fun DBAudioData.toAudioEntity(): AudioEntity {
    return AudioEntity(
         album = album, uri = uri, songName = songName, duration = duration, artist = artist,
        size = size, albumThumbnailUri = albumThumbnailUri, isFavorite = isFavorite, isInternal = isInternal, isOwned = isOwned, externalId = externalId
    )
}

internal fun AudioEntity.toDBAudio(): DBAudioData {
    return DBAudioData(
        idAudio = id, album = album, uri = uri, songName = songName, duration = duration, artist = artist,
        size = size, albumThumbnailUri = albumThumbnailUri, isFavorite = isFavorite, isInternal = isInternal, isOwned = isOwned, externalId = externalId
    )
}

internal fun SearchAudio.toQueryAudio(audioDao: IAudioDao): AudioQuery {
    return when(this){
        is SearchAudio.SearchByAlbum-> AudioQuery.FindBayAlbumQuery(audioDao, album)
        is SearchAudio.SearchByArtist-> AudioQuery.FindByArtistNameObject(audioDao, artist)
        is SearchAudio.SearchBySongName-> AudioQuery.FindBySongNameQuery(audioDao, songName)
    }
}