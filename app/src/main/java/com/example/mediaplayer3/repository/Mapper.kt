package com.example.mediaplayer3.repository

import android.net.Uri
import com.example.mediaplayer3.data.entity.MPAppAudio
import com.example.mediaplayer3.data.entity.MPAppPlayList
import com.example.mediaplayer3.data.entity.RepeatMode
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.domain.entity.UiPlayList
import com.example.mpstorage.database.data.DBAudioData
import com.example.mpstorage.database.data.DBPlayListData


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

fun RepeatMode.toAnnotation():@com.example.mpcore.api.data.datastore.data.RepeatMode Int{
    return when(this){
        RepeatMode.NO_REPEAT-> com.example.mpdataprovider.datastore.data.RepeatMode.NO_REPEAT
        RepeatMode.REPEAT_ALL-> com.example.mpdataprovider.datastore.data.RepeatMode.REPEAT_ALL
        RepeatMode.ONE_REPEAT-> com.example.mpdataprovider.datastore.data.RepeatMode.ONE_REPEAT
    }
}

fun @com.example.mpcore.api.data.datastore.data.RepeatMode Int.toEnum(): RepeatMode{
    return when(this){
        com.example.mpdataprovider.datastore.data.RepeatMode.NO_REPEAT->RepeatMode.NO_REPEAT
        com.example.mpdataprovider.datastore.data.RepeatMode.ONE_REPEAT->RepeatMode.ONE_REPEAT
        com.example.mpdataprovider.datastore.data.RepeatMode.REPEAT_ALL->RepeatMode.REPEAT_ALL
        else -> RepeatMode.NO_REPEAT
    }
}

fun DBPlayListData.toMPAppPlayList(): MPAppPlayList{
    return MPAppPlayList(
        id = playListId,
        name = playListName
    )
}

fun MPAppPlayList.toDBPlayList(): DBPlayListData{
    return DBPlayListData(
        playListId = id,
        playListName = name
    )
}

fun MPAppPlayList.toUiPlayList(thumbnailUri: Uri? = null): UiPlayList{
    return UiPlayList(
        playListId = id,
        playListName = name,
        thumbnailUri = thumbnailUri
    )
}

fun UiPlayList.toMPAppPlayList(): MPAppPlayList{
    return MPAppPlayList(
        id = playListId,
        name = playListName
    )
}