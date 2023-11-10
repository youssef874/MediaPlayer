package com.example.mpdataprovider.datadtore.internal

import com.example.mpdataprovider.datadtore.IDataStoreController
import com.example.mpdataprovider.datadtore.RepeatMode

internal interface IAudioDataStoreProvider {

    fun lastPlayingSong(): IDataStoreController<Long>

    fun lastPlayingSongLastDuration(): IDataStoreController<Int>

    fun isInRandomMode(): IDataStoreController<Boolean>

    fun RepeatMode(): IDataStoreController<@RepeatMode Int>
}