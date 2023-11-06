package com.example.mpdataprovider.DataStore

import android.content.Context
import com.example.mpdataprovider.DataStore.factory.DataStoreProviderFactory

object AudioDataStoreApi {

    /**
     * Update and get lastPlayingSong from data store
     *@param context: Android context
     * @return custom implementation of [IDataStoreController]
     */
    fun lastPlayingSong(context: Context): IDataStoreController<Long>{
        return DataStoreProviderFactory.create(context).lastPlayingSong()
    }

    /**
     * Update and get lastPlayingSongDuration from data store
     *@param context: Android context
     * @return custom implementation of [IDataStoreController]
     */
    fun lastPlayingSongLastDuration(context: Context): IDataStoreController<Int>{
        return DataStoreProviderFactory.create(context).lastPlayingSongLastDuration()
    }

    /**
     * Update and get isInRandom from data store
     *@param context: Android context
     * @return custom implementation of [IDataStoreController]
     */
    fun isInRandomMode(context: Context): IDataStoreController<Boolean>{
        return DataStoreProviderFactory.create(context).isInRandomMode()
    }

    /**
     * Update and get repeatMode from data store
     *@param context: Android context
     * @return custom implementation of [IDataStoreController]
     */
    fun repeatMode(context: Context): IDataStoreController<@RepeatMode Int>{
        return DataStoreProviderFactory.create(context).RepeatMode()
    }
}