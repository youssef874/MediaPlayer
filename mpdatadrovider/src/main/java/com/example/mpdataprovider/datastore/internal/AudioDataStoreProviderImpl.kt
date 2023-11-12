package com.example.mpdataprovider.datastore.internal

import com.example.mpdataprovider.datastore.IDataStoreController
import com.example.mpdataprovider.datastore.RepeatMode
import com.example.mpdataprovider.datastore.data.DataStoreModel
import kotlinx.coroutines.flow.Flow

internal class AudioDataStoreProviderImpl(private var audioDataStore: IAudioDataStore) :
    IAudioDataStoreProvider {

    private val lastPlayingSongModel = DataStoreModel(
        key = "lastPlayingSong",
        defaultValue = -1L
    )

    override fun lastPlayingSong(): IDataStoreController<Long> {
        return object : IDataStoreController<Long> {
            override suspend fun updateValue(value: Long) {
                audioDataStore.putLong(lastPlayingSongModel.key, value)
            }

            override fun getValue(): Flow<Long> {
                return audioDataStore.getLong(
                    lastPlayingSongModel.key,
                    lastPlayingSongModel.defaultValue
                )
            }

        }
    }

    private val lastPlayingSongLastDurationModel = DataStoreModel(
        key = "lastPlayingSongDuration",
        defaultValue = -1
    )

    override fun lastPlayingSongLastDuration(): IDataStoreController<Int> {
        return object : IDataStoreController<Int> {
            override suspend fun updateValue(value: Int) {
                audioDataStore.putInt(lastPlayingSongLastDurationModel.key, value)
            }

            override fun getValue(): Flow<Int> {
                return audioDataStore.getInt(
                    lastPlayingSongLastDurationModel.key,
                    lastPlayingSongLastDurationModel.defaultValue
                )
            }

        }
    }

    private val isRandomisedMode = DataStoreModel(
        key = "is_in_randomMode",
        defaultValue = false
    )

    override fun isInRandomMode(): IDataStoreController<Boolean> {
        return object : IDataStoreController<Boolean>{
            override suspend fun updateValue(value: Boolean) {
                audioDataStore.putBoolean(isRandomisedMode.key,value)
            }

            override fun getValue(): Flow<Boolean> {
                return audioDataStore.getBoolean(
                    isRandomisedMode.key,
                    isRandomisedMode.defaultValue
                )
            }

        }
    }

    private val repeatMode = DataStoreModel(
        key = "Repeat_mode",
        defaultValue = RepeatMode.NO_REPEAT
    )

    override fun repeatMode(): IDataStoreController<Int> {
        return object : IDataStoreController<@RepeatMode Int>{
            override suspend fun updateValue(@RepeatMode value: Int) {
                audioDataStore.putInt(repeatMode.key,value)
            }

            override fun getValue(): Flow<@RepeatMode Int> {
                return audioDataStore.getInt(repeatMode.key,repeatMode.defaultValue)
            }

        }
    }

    private val isSynchronisationFinishedModel = DataStoreModel(
        key = "is_synchronisation_finished",
        defaultValue = false
    )

    override fun isSynchronisationFinished(): IDataStoreController<Boolean> {
        return object : IDataStoreController<Boolean>{
            override suspend fun updateValue(value: Boolean) {
                audioDataStore.putBoolean(isSynchronisationFinishedModel.key,value)
            }

            override fun getValue(): Flow<Boolean> {
                return audioDataStore.getBoolean(isSynchronisationFinishedModel.key,isSynchronisationFinishedModel.defaultValue)
            }

        }
    }

    private val isSynchronisationWithContentProviderCompletedModel = DataStoreModel(
        key = "is_synchronisation_with_content_provider_completed",
        defaultValue = false
    )

    override fun isSynchronisationWithContentProviderCompleted(): IDataStoreController<Boolean> {
        return object : IDataStoreController<Boolean>{
            override suspend fun updateValue(value: Boolean) {
                audioDataStore.putBoolean(isSynchronisationWithContentProviderCompletedModel.key,value)
            }

            override fun getValue(): Flow<Boolean> {
                return audioDataStore.getBoolean(
                    isSynchronisationWithContentProviderCompletedModel.key,
                    isSynchronisationWithContentProviderCompletedModel.defaultValue
                )
            }

        }
    }

}