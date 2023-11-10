package com.example.mpdataprovider.contentprovider

import android.Manifest
import android.content.Context
import com.example.mpdataprovider.contentprovider.data.MPAudio
import com.example.mpdataprovider.contentprovider.data.MissingPermissionException
import com.example.mpdataprovider.contentprovider.internal.factory.AudioProviderFactoryImpl

object AudioApi {

    /**
     * Get all existing songs in android device
     * @param context: Android context
     * @return list of [MPAudio]
     * @throws [MissingPermissionException] if the user di not grant permission for
     * [Manifest.permission.READ_MEDIA_AUDIO] for android devises with api version more than 33
     * or [Manifest.permission.READ_EXTERNAL_STORAGE] for older version
     */
    suspend fun getAllSongs(context: Context): List<MPAudio>{
        return AudioProviderFactoryImpl.create(context).getAllSongs(context)
    }

    /**
     * Get all existing songs in android device with the provided album
     * @param context: Android context
     * @param album: the album which the result will be filtered with
     * @return list of [MPAudio]
     * @throws [MissingPermissionException] if the user di not grant permission for
     * [Manifest.permission.READ_MEDIA_AUDIO] for android devises with api version more than 33
     * or [Manifest.permission.READ_EXTERNAL_STORAGE] for older version
     */
    suspend fun getSongsByAlbum(context: Context,album: String): List<MPAudio>{
        return AudioProviderFactoryImpl.create(context).getAllSongsByAlbum(context, album)
    }

    /**
     * Get all existing songs in android device with the provided song name
     * @param context: Android context
     * @param songName: the song name which the result will be filtered with
     * @return list of [MPAudio]
     * @throws [MissingPermissionException] if the user di not grant permission for
     * [Manifest.permission.READ_MEDIA_AUDIO] for android devises with api version more than 33
     * or [Manifest.permission.READ_EXTERNAL_STORAGE] for older version
     */
    suspend fun getSongsBySongName(context: Context, songName: String): List<MPAudio>{
        return AudioProviderFactoryImpl.create(context).getAllSongsBySongName(context, songName)
    }

    /**
     * Get all existing songs in android device with the provided artist
     * @param context: Android context
     * @param artist: the artist which the result will be filtered with
     * @return list of [MPAudio]
     * @throws [MissingPermissionException] if the user di not grant permission for
     * [Manifest.permission.READ_MEDIA_AUDIO] for android devises with api version more than 33
     * or [Manifest.permission.READ_EXTERNAL_STORAGE] for older version
     */
    suspend fun getSongsByArtist(context: Context, artist: String): List<MPAudio>{
        return AudioProviderFactoryImpl.create(context).getAllSongByArtist(context,artist)
    }

    /**
     * Get the song in android device with the provided id
     * @param context: Android context
     * @param id: the the song id which the result will be filtered with
     * @return instance of [MPAudio]
     * @throws [MissingPermissionException] if the user di not grant permission for
     * [Manifest.permission.READ_MEDIA_AUDIO] for android devises with api version more than 33
     * or [Manifest.permission.READ_EXTERNAL_STORAGE] for older version
     */
    suspend fun getSongById(context: Context,id: Long): MPAudio?{
        return AudioProviderFactoryImpl.create(context).getSongById(context, id)
    }
}