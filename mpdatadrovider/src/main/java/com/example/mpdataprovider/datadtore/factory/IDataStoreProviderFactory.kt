package com.example.mpdataprovider.datadtore.factory

import android.content.Context
import com.example.mpdataprovider.datadtore.internal.IAudioDataStoreProvider

internal interface IDataStoreProviderFactory {

    fun create(context: Context): IAudioDataStoreProvider
}