package com.example.mpdataprovider.DataStore.factory

import android.content.Context
import com.example.mpdataprovider.DataStore.internal.AudioDataStoreProviderImpl
import com.example.mpdataprovider.DataStore.internal.IAudioDataStoreProvider

internal object DataStoreProviderFactory: IDataStoreProviderFactory {
    override fun create(context: Context): IAudioDataStoreProvider {
        return AudioDataStoreProviderImpl(
            DataStoreFactoryImpl.create(context)
        )
    }
}