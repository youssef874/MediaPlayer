package com.example.mpmediamanager.internal

import android.content.Context
import android.net.Uri


internal interface IAudioPlayerManager {

    /**
     * Play the requested song if is not playing
     * @param context: Android context
     * @param uri: The song uri
     */
    fun playSong(context: Context, uri: Uri,seekTo: Int = -1)

    /**
     * Stop the requested song if is Playing
     * @param context: Android context
     * @param uri: The song uri
     */
    fun stopSong(context: Context, uri: Uri)

    /**
     * pause the current song playing song
     * @param context: Android context
     */
    fun pauseSong(context: Context)

    /**
     * resume the current song
     * @param context: Android context
     */
    fun resumeSong(context: Context,seekTo: Int = -1)

    /**
     * forward song by the provided duration or 30 s by default
     * @param forwardWith: the duration to forward
     */
    fun forward(forwardWith: Int = Constant.DELTA_TIME)

    /**
     * Rewind song by the provided duration or 30 s by default
     * @param rewindWith: the duration to rewind
     */
    fun rewind(rewindWith: Int = Constant.DELTA_TIME)

    /**
     * Update the [MediaPlayer.seekTo()]
     * @param context: Android context to
     * @param uri: Android song uri
     * @param position: the position which will update in
     */
    fun updatePlayerPosition(context: Context,uri: Uri,position: Int)
}