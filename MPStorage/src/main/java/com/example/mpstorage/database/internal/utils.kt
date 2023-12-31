package com.example.mpstorage.database.internal

import android.net.Uri
import com.example.mpstorage.database.data.DBAudioData
import com.example.mpstorage.database.data.QueryAudio
import com.example.mpstorage.database.data.SearchAudio
import com.example.mpstorage.database.internal.entity.AudioEntity


internal fun DBAudioData.toAudioEntity(): AudioEntity {
    return AudioEntity(
        album = album,
        uri = uri.toString(),
        songName = songName,
        duration = duration,
        artist = artist,
        size = size,
        albumThumbnailUri = albumThumbnailUri?.toString() ?: "",
        isFavorite = isFavorite,
        isInternal = isInternal,
        isOwned = isOwned,
        externalId = externalId
    )
}

internal fun AudioEntity.toDBAudio(): DBAudioData {
    return DBAudioData(
        idAudio = id,
        album = album,
        uri = Uri.parse(uri),
        songName = songName,
        duration = duration,
        artist = artist,
        size = size,
        albumThumbnailUri = if (albumThumbnailUri.isNotBlank()) Uri.parse(albumThumbnailUri) else null,
        isFavorite = isFavorite,
        isInternal = isInternal,
        isOwned = isOwned,
        externalId = externalId
    )
}

internal fun SearchAudio.toBaseQueryAudio(audioDao: IAudioDao): BaseAudioQuery {
    return when (this) {
        is SearchAudio.SearchByAlbum -> BaseAudioQuery.FindBayAlbumQueryBase(audioDao, album)
        is SearchAudio.SearchByArtist -> BaseAudioQuery.FindByArtistNameObject(audioDao, artist)
        is SearchAudio.SearchBySongName -> BaseAudioQuery.FindBySongNameQueryBase(audioDao, songName)
    }
}

internal fun QueryAudio.toInternalQueryAudio(audioDao: IAudioDao): InternalAudioQuery{
    return when(this){
        is QueryAudio.ChaneIsFavorite->InternalAudioQuery.ChangeIsFavorite(audioDao,songId,isFavorite)
    }
}