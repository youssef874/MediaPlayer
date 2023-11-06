package com.example.mpdataprovider.ContentProvider.internal

import android.content.Context
import android.util.Log
import com.example.mpdataprovider.ContentProvider.data.MPAudio
import com.example.mpdataprovider.ContentProvider.data.MissingPermissionException
import com.example.mpdataprovider.ContentProvider.data.UpdateException
import com.example.mplog.MPLogger

internal class AudioProviderImpl(
    private val audioExtractor: IAudioExtractor,
    private val audioConfiguration: IAudioConfiguration
): IAudioProvider {


    override suspend fun getAllSongs(context: Context): List<MPAudio> {
        MPLogger.d(CLASS_NAME,"getAllSongs", TAG,"Start loading all audio")
        with(audioConfiguration.askRequiredPermission(context)){
            if (isNotEmpty()){
                MPLogger.e(CLASS_NAME,"getAllSongs", TAG,"missing permissions $this")
                throw MissingPermissionException(this)
            }
        }
        try {
            audioExtractor.loadAllAudio()
            val result = audioExtractor.getAllAudio()
            MPLogger.d(CLASS_NAME,"getAllSongs", TAG,"Successfully get all songs ${result.size}")
            return result
        }catch (e: Exception){
            MPLogger.e(CLASS_NAME,"getAllSongs", TAG,"Unexpected error ${e.message}")
            throw e
        }
    }

    override
    suspend fun getAllSongByArtist(context: Context, artistName: String): List<MPAudio> {
        MPLogger.d(CLASS_NAME,"getAllSongByArtist", TAG,"Start loading all audio artistName: $artistName")
        with(audioConfiguration.askRequiredPermission(context)){
            if (isNotEmpty()){
                MPLogger.e(CLASS_NAME,"getAllSongByArtist", TAG,"missing permissions $this")
                throw MissingPermissionException(this)
            }
        }
        try {
            audioExtractor.loadAllAudio()
            val result = audioExtractor.getAllAudio().filter { it.artistName == artistName }
            MPLogger.d(CLASS_NAME,"getAllSongByArtist", TAG,"Successfully get all songs ${result.size}")
            return result
        }catch (e: Exception){
            MPLogger.e(CLASS_NAME,"getAllSongsBySongName", TAG,"Unexpected error ${e.message}")
            throw e
        }
    }

    override suspend fun getAllSongsByAlbum(context: Context, album: String): List<MPAudio> {
        MPLogger.d(CLASS_NAME,"getAllSongsByAlbum", TAG,"Start loading all audio album: $album")
        with(audioConfiguration.askRequiredPermission(context)){
            if (isNotEmpty()){
                MPLogger.e(CLASS_NAME,"getAllSongsByAlbum", TAG,"missing permissions $this")
                throw MissingPermissionException(this)
            }
        }
        try {
            audioExtractor.loadAllAudio()
            val result = audioExtractor.getAllAudio().filter { it.album == album }
            MPLogger.d(CLASS_NAME,"getAllSongsByAlbum", TAG,"Successfully get all songs ${result.size}")
            return result
        }catch (e: Exception){
            MPLogger.e(CLASS_NAME,"getAllSongsByAlbum", TAG,"Unexpected error ${e.message}")
            throw e
        }
    }

    override suspend fun getAllSongsBySongName(context: Context, songName: String): List<MPAudio> {
        MPLogger.d(CLASS_NAME,"getAllSongsBySongName", TAG,"Start loading all audio songName: $songName")
        with(audioConfiguration.askRequiredPermission(context)){
            if (isNotEmpty()){
                MPLogger.e(CLASS_NAME,"getAllSongsBySongName", TAG,"missing permissions $this")
                throw MissingPermissionException(this)
            }
        }
        try {
            audioExtractor.loadAllAudio()
            val result = audioExtractor.getAllAudio().filter { it.songName == songName }
            MPLogger.d(CLASS_NAME,"getAllSongsBySongName", TAG,"Successfully get all songs ${result.size}")
            return result
        }catch (e: Exception){
            MPLogger.e(CLASS_NAME,"getAllSongsBySongName", TAG,"Unexpected error ${e.message}")
            throw e
        }
    }

    override
    suspend fun getSongById(context: Context, id: Long): MPAudio? {
        MPLogger.d(CLASS_NAME,"getSongById", TAG,"Start loading all audio id: $id")
        with(audioConfiguration.askRequiredPermission(context)){
            if (isNotEmpty()){
                MPLogger.e(CLASS_NAME,"getSongById", TAG,"missing permissions $this")
                throw MissingPermissionException(this)
            }
        }
        try {
            audioExtractor.loadAllAudio()
            val result = audioExtractor.getAllAudio().firstOrNull { it.id == id }
            MPLogger.d(CLASS_NAME,"getAllSongs", TAG,"Successfully get all songs ${result?.size}")
            return result
        }catch (e: Exception){
            MPLogger.e(CLASS_NAME,"getSongById", TAG,"Unexpected error ${e.message}")
            throw e
        }
    }

    companion object{
        private const val CLASS_NAME = "AudioProviderImpl"
        private const val TAG = "CONTENT_PROVIDER"
    }
}