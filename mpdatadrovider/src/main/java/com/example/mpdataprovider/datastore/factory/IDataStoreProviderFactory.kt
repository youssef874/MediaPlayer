package com.example.mpdataprovider.datastore.factory

import android.content.Context
import com.example.mpdataprovider.datastore.internal.IAudioDataStoreProvider

internal interface IDataStoreProviderFactory {

    fun create(context: Context): IAudioDataStoreProvider
}