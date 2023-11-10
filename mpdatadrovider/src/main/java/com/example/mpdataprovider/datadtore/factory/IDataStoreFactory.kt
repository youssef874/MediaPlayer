package com.example.mpdataprovider.datadtore.factory

import android.content.Context
import com.example.mpdataprovider.datadtore.internal.IAudioDataStore

internal interface IDataStoreFactory {

    fun create(context: Context): IAudioDataStore
}