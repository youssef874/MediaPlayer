package com.example.mpdataprovider

fun getAlbumThumbnailPath(albumId: Long): String{
    return "content://media/external/audio/albumart/$albumId"
}