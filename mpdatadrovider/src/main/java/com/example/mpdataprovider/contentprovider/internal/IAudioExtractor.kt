package com.example.mpdataprovider.contentprovider.internal

import com.example.mpdataprovider.contentprovider.data.MPAudio
import kotlinx.coroutines.Dispatchers

/**
 * This interface represent the abstraction for retrieving al audio
 * file in the android device and provide the to the application
 */
internal interface IAudioExtractor {

    /**
     * Finding all the audio files in the device and cache them this operation
     * done in [Dispatchers.IO]
     */
    suspend fun loadAllAudio()

    fun setOnDataChangesListener(onDataChanges: ()->Unit)

    /**
     * get the cached list
     * @return list of [MPAudio]
     */
    fun getAllAudio(): List<MPAudio>
}