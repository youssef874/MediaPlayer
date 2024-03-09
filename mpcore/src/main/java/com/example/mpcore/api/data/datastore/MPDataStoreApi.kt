package com.example.mpcore.api.data.datastore

import android.content.Context
import com.example.mpcore.api.data.datastore.data.RepeatMode
import com.example.mpcore.internal.data.datastore.factory.DataStoreProviderFactoryImpl

object MPDataStoreApi {


    /**
     * Update and get lastPlayingSong from data store
     *@param context: Android context
     * @return custom implementation of [IMPDataStoreManager]
     */
    fun lastPlayingSong(context: Context): IMPDataStoreManager<Long> {
        return DataStoreProviderFactoryImpl.create().lastPlayingSong(context)
    }

    /**
     * Update and get lastPlayingSongDuration from data store
     *@param context: Android context
     * @return custom implementation of [IMPDataStoreManager]
     */
    fun lastPlayingSongLastDuration(context: Context): IMPDataStoreManager<Int> {
        return DataStoreProviderFactoryImpl.create().lastPlayingSongLastDuration(context)
    }

    /**
     * Update and get isInRandom from data store
     *@param context: Android context
     * @return custom implementation of [IMPDataStoreManager]
     */
    fun isInRandomMode(context: Context): IMPDataStoreManager<Boolean> {
        return DataStoreProviderFactoryImpl.create().isInRandomMode(context)
    }



    /**
     * Update and get repeatMode from data store
     *@param context: Android context
     * @return custom implementation of [IMPDataStoreManager]
     */
    fun repeatMode(context: Context): IMPDataStoreManager<@RepeatMode Int> {
        return DataStoreProviderFactoryImpl.create().repeatMode(context)
    }
}