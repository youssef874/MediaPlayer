package com.example.mpdataprovider.datastore.internal

import com.example.mpdataprovider.datastore.IDataStoreController
import com.example.mpdataprovider.datastore.data.RepeatMode

internal interface IAudioDataStoreProvider {

    fun lastPlayingSong(): IDataStoreController<Long>

    fun lastPlayingSongLastDuration(): IDataStoreController<Int>

    fun isInRandomMode(): IDataStoreController<Boolean>

    fun repeatMode(): IDataStoreController<@RepeatMode Int>

    fun isSynchronisationFinished(): IDataStoreController<Boolean>

    fun isSynchronisationWithContentProviderCompleted(): IDataStoreController<Boolean>
}