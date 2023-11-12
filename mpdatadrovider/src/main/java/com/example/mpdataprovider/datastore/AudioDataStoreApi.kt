package com.example.mpdataprovider.datastore

import android.content.Context
import com.example.mpdataprovider.datastore.factory.DataStoreProviderFactory

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
        return DataStoreProviderFactory.create(context).repeatMode()
    }

    /**
     * Update and get isSynchronisationFinished from data store
     *@param context: Android context
     * @return custom implementation of [IDataStoreController]
     */
    fun isSynchronisationFinished(context: Context): IDataStoreController<Boolean>{
        return DataStoreProviderFactory.create(context).isSynchronisationFinished()
    }

    /**
     * Update and get isSynchronisationWithContentProviderCompleted from data store
     *@param context: Android context
     * @return custom implementation of [IDataStoreController]
     */
    fun isSynchronisationWithContentProviderCompleted(context: Context): IDataStoreController<Boolean>{
        return DataStoreProviderFactory.create(context).isSynchronisationWithContentProviderCompleted()
    }
}