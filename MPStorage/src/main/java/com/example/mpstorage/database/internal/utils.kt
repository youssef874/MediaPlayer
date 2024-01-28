package com.example.mpstorage.database.internal

import android.net.Uri
import com.example.mpstorage.database.data.DBAudioData
import com.example.mpstorage.database.data.DBPlayListData
import com.example.mpstorage.database.data.PlayListQuery
import com.example.mpstorage.database.data.QueryAudio
import com.example.mpstorage.database.data.SearchAudio
import com.example.mpstorage.database.data.SearchPlayList
import com.example.mpstorage.database.internal.entity.AudioEntity
import com.example.mpstorage.database.internal.entity.PlayListEntity


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

internal fun SearchAudio.toIAudioOneShotFinder(audioDao: IAudioDao): IAudioOneShotFinder{
    return when(this){
        is SearchAudio.GetFirstSongInPlaylist->BaseAudioQuery.FindFirstPlaylistAudio(audioDao,playListId)
        else->throw IllegalArgumentException("Wrong query")
    }
}

internal fun SearchAudio.toIAudioRealTimeListFinder(audioDao: IAudioDao): IAudioRealTimeListFinder{
    return when(this){
        is SearchAudio.SearchByAlbum->BaseAudioQuery.FindBayAlbumQueryBase(audioDao,album)
        is SearchAudio.SearchByArtist -> BaseAudioQuery.FindByArtistNameObject(audioDao, artist)
        is SearchAudio.SearchBySongName -> BaseAudioQuery.FindBySongNameQueryBase(audioDao, songName)
        is SearchAudio.SearchForAllSongForPlayList->BaseAudioQuery.FindAllSongForPlayList(audioDao,playListId)
        else->throw IllegalArgumentException("Wrong query")
    }
}

internal fun DBPlayListData.toPlayListEntity(): PlayListEntity{
    return PlayListEntity(
        id = playListId,
        name = playListName
    )
}

internal fun PlayListEntity.toDBPlayListData(): DBPlayListData{
    return DBPlayListData(
        playListId = id,
        playListName = name
    )
}

internal fun SearchPlayList.toIPlaylistRealTimeListFinder(playListDao: IPlayListDao): IPlaylistRealTimeListFinder{
    return when(this){
        is SearchPlayList.SearchByName->InternalPlayListQuery.FindPlayListByName(playListDao,playListName)
        is SearchPlayList.SearchSongPlayLists->InternalPlayListQuery.FindAllPlayListForAudio(playListDao,songId)
    }
}

internal fun QueryAudio.toIOperationQuery(audioDao: IAudioDao): IOperationQuery{
    return when(this){
        is QueryAudio.ChaneIsFavorite->BaseAudioQuery.ChangeIsFavorite(audioDao,songId,isFavorite)
    }
}

internal fun PlayListQuery.toIOperationQuery(playListDao: IPlayListDao): IOperationQuery{
    return when(this){
        is PlayListQuery.AddSongToPlayList->InternalPlayListQuery.AttachAudioToPlayList(playListDao,songId,playList)
    }
}