package com.example.mpcore.api.media

import android.content.Context
import android.net.Uri
import com.example.mpmediamanager.MpAudioManagerApi
import kotlinx.coroutines.flow.Flow

object MPMediaPlayer {

    /**
     * Play the requested song if is not playing
     * @param context: Android context
     * @param uri: The song uri
     */
    fun playSong(context: Context, uri: Uri, seekTo: Int = -1){
        MpAudioManagerApi.playSong(context, uri, seekTo)
    }

    /**
     * Stop the requested song if is Playing
     * @param context: Android context
     * @param uri: The song uri
     */
    fun stopSong(context: Context,uri: Uri){
        MpAudioManagerApi.stopSong(context, uri)
    }

    /**
     * pause the current song playing song
     * @param context: Android context
     */
    fun pauseSong(context: Context){
        MpAudioManagerApi.pauseSong(context)
    }

    /**
     * resume the current song
     * @param context: Android context
     */
    fun resumeSong(context: Context,seekTo: Int = -1){
        MpAudioManagerApi.resumeSong(context, seekTo)
    }

    /**
     * Listen to current playing son completion
     * @param callback: callback will be invoked when current playing audio completed
     */
    fun observeSongCompletion(callback: ()->Unit){
        MpAudioManagerApi.observeSongCompletion(callback)
    }

    /**
     * Listen to current playing song progression
     * @return [Flow] of current media position
     */
    fun observeDurationProgress(): Flow<Int> {
        return MpAudioManagerApi.observeDurationProgress()
    }

    /**
     * Forward the current playing
     * @param forwardWith: the amount to forward with or 3à by default
     */
    fun forward(forwardWith: Int = 30){
        MpAudioManagerApi.forward(forwardWith)
    }

    /**
     * Rewind the current playing
     * @param rewindWith: the amount to rewind with or 3à by default
     */
    fun rewind(rewindWith: Int){
        MpAudioManagerApi.rewind(rewindWith)
    }

    /**
     * Update the song playing position
     * @param context: Android context to
     * @param uri: Android song uri
     * @param position: the position which will update in
     */
    fun setThePositionToPlayWith(context: Context,uri: Uri,position: Int){
        MpAudioManagerApi.setThePositionToPlayWith(context, uri, position)
    }
}