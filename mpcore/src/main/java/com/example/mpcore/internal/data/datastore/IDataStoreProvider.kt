package com.example.mpcore.internal.data.datastore

import android.content.Context
import com.example.mpcore.api.data.datastore.IMPDataStoreManager
import com.example.mpcore.api.data.datastore.data.RepeatMode

internal interface IDataStoreProvider {

    fun lastPlayingSong(context: Context): IMPDataStoreManager<Long>

    fun lastPlayingSongLastDuration(context: Context): IMPDataStoreManager<Int>

    fun isInRandomMode(context: Context): IMPDataStoreManager<Boolean>

    fun repeatMode(context: Context): IMPDataStoreManager<@RepeatMode Int>
}