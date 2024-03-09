package com.example.mpcore.internal.data.datastore

import android.content.Context
import com.example.mpcore.api.data.datastore.IMPDataStoreManager
import com.example.mpdataprovider.datastore.AudioDataStoreApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal object DataStoreProviderImpl: IDataStoreProvider {
    override fun lastPlayingSong(context: Context): IMPDataStoreManager<Long> {
        val lastPlayingSongController = AudioDataStoreApi.lastPlayingSong(context)
       return object: IMPDataStoreManager<Long>{
           override suspend fun updateValue(data: Long) {
               lastPlayingSongController.updateValue(data)
           }

           override fun observeValue(): Flow<Long> {
               return lastPlayingSongController.getValue()
           }

       }
    }

    override fun lastPlayingSongLastDuration(context: Context): IMPDataStoreManager<Int> {
        val lastPlayingSongLastDurationController = AudioDataStoreApi.lastPlayingSongLastDuration(context)
        return object : IMPDataStoreManager<Int>{
            override suspend fun updateValue(data: Int) {
                lastPlayingSongLastDurationController.updateValue(data)
            }

            override fun observeValue(): Flow<Int> {
                return lastPlayingSongLastDurationController.getValue()
            }

        }
    }

    override fun isInRandomMode(context: Context): IMPDataStoreManager<Boolean> {
        val isInRandomModeController = AudioDataStoreApi.isInRandomMode(context)
        return object : IMPDataStoreManager<Boolean>{
            override suspend fun updateValue(data: Boolean) {
                isInRandomModeController.updateValue(data)
            }

            override fun observeValue(): Flow<Boolean> {
                return isInRandomModeController.getValue()
            }

        }
    }

    override fun repeatMode(context: Context): IMPDataStoreManager<Int> {
        val repeatModeController = AudioDataStoreApi.repeatMode(context)
        return object : IMPDataStoreManager<Int>{
            override suspend fun updateValue(data: Int) {
                repeatModeController.updateValue(data.toDataProviderRepeatMode())
            }

            override fun observeValue(): Flow<Int> {
                return repeatModeController.getValue().map { it.toThisModuleRepeatMode() }
            }

        }
    }
}