package com.example.mpdataprovider.DataStore.factory

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.mpdataprovider.DataStore.internal.AudioDataStoreImpl
import com.example.mpdataprovider.DataStore.internal.IAudioDataStore

internal object DataStoreFactoryImpl : IDataStoreFactory {

    @Volatile
    private var dataStore: DataStore<Preferences>? = null

    override fun create(context: Context): IAudioDataStore {
        val sDataStore = dataStore ?: synchronized(this) {
            val data = PreferenceDataStoreFactory.create {
                val path = AudioDataStoreImpl.FILE_NAME
                context.preferencesDataStoreFile(path)
            }
            dataStore = data
            data
        }
        return AudioDataStoreImpl(dataStore = sDataStore)
    }
}