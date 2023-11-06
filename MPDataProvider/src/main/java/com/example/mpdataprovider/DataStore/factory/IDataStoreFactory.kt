package com.example.mpdataprovider.DataStore.factory

import android.content.Context
import com.example.mpdataprovider.DataStore.internal.IAudioDataStore

internal interface IDataStoreFactory {

    fun create(context: Context): IAudioDataStore
}