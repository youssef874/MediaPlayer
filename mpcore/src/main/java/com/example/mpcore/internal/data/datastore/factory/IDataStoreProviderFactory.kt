package com.example.mpcore.internal.data.datastore.factory

import com.example.mpcore.internal.data.datastore.IDataStoreProvider

internal interface IDataStoreProviderFactory {

    fun create(): IDataStoreProvider
}