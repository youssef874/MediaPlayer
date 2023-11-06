package com.example.mpmediamanager

import android.content.Context
import android.net.Uri
import com.example.mpmediamanager.factory.AudioManagerFactoryImpl

object MpAudioManagerApi {

    /**
     * Play the requested song if is not playing
     * @param context: Android context
     * @param uri: The song uri
     */
    fun playSong(context: Context, uri: Uri,seekTo: Int = -1){
        AudioManagerFactoryImpl.create().playSong(context, uri,seekTo)
    }

    /**
     * Stop the requested song if is Playing
     * @param context: Android context
     * @param uri: The song uri
     */
    fun stopSong(context: Context,uri: Uri){
        AudioManagerFactoryImpl.create().stopSong(context, uri)
    }

    /**
     * pause the current song playing song
     * @param context: Android context
     */
    fun pauseSong(context: Context){
        AudioManagerFactoryImpl.create().pauseSong(context)
    }

    /**
     * resume the current song
     * @param context: Android context
     */
    fun resumeSong(context: Context,seekTo: Int = -1){
        AudioManagerFactoryImpl.create().resumeSong(context,seekTo)
    }

    /**
     * Listen to current playing son completion
     */
    fun setSongCompleteListener(onComplete: () -> Unit){
        AudioManagerFactoryImpl.create().setSongCompleteListener{
            onComplete()
        }
    }

    /**
     * Listen to current playing song progression
     */
    fun setOnDurationProgressListener(onDurationProgressListener: (duration: Int) -> Unit){
        AudioManagerFactoryImpl.create().setOnDurationProgressListener(onDurationProgressListener)
    }

    /**
     * Forward the current playing
     * @param forwardWith: the amount to forward with or 3à by default
     */
    fun forward(forwardWith: Int = 30){
        AudioManagerFactoryImpl.create().forward(forwardWith)
    }

    /**
     * Rewind the current playing
     * @param rewindWith: the amount to rewind with or 3à by default
     */
    fun rewind(rewindWith: Int){
        AudioManagerFactoryImpl.create().rewind(rewindWith)
    }

    /**
     * Update the song playing position
     * @param context: Android context to
     * @param uri: Android song uri
     * @param position: the position which will update in
     */
    fun setThePositionToPlayWith(context: Context,uri: Uri,position: Int){
        AudioManagerFactoryImpl.create().updatePlayerPosition(context, uri, position)
    }
}