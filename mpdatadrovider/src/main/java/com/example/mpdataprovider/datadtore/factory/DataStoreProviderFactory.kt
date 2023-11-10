package com.example.mpdataprovider.datadtore.factory

import android.content.Context
import com.example.mpdataprovider.datadtore.internal.AudioDataStoreProviderImpl
import com.example.mpdataprovider.datadtore.internal.IAudioDataStoreProvider

internal object DataStoreProviderFactory: IDataStoreProviderFactory {
    override fun create(context: Context): IAudioDataStoreProvider {
        return AudioDataStoreProviderImpl(
            DataStoreFactoryImpl.create(context)
        )
    }
}