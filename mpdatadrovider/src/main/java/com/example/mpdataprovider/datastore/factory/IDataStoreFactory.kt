package com.example.mpdataprovider.datastore.factory

import android.content.Context
import com.example.mpdataprovider.datastore.internal.IAudioDataStore

internal interface IDataStoreFactory {

    fun create(context: Context): IAudioDataStore
}