package com.example.mpdataprovider.DataStore.internal

import android.content.Context
import com.example.mpdataprovider.DataStore.IDataStoreController
import com.example.mpdataprovider.DataStore.RepeatMode

internal interface IAudioDataStoreProvider {

    fun lastPlayingSong(): IDataStoreController<Long>

    fun lastPlayingSongLastDuration(): IDataStoreController<Int>

    fun isInRandomMode(): IDataStoreController<Boolean>

    fun RepeatMode(): IDataStoreController<@RepeatMode Int>
}