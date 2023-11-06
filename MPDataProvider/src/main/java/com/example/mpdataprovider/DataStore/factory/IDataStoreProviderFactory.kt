package com.example.mpdataprovider.DataStore.factory

import android.content.Context
import com.example.mpdataprovider.DataStore.internal.IAudioDataStoreProvider

internal interface IDataStoreProviderFactory {

    fun create(context: Context): IAudioDataStoreProvider
}