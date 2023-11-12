package com.example.mpdataprovider.datastore.factory

import android.content.Context
import com.example.mpdataprovider.datastore.internal.AudioDataStoreProviderImpl
import com.example.mpdataprovider.datastore.internal.IAudioDataStoreProvider

internal object DataStoreProviderFactory: IDataStoreProviderFactory {
    override fun create(context: Context): IAudioDataStoreProvider {
        return AudioDataStoreProviderImpl(
            DataStoreFactoryImpl.create(context)
        )
    }
}