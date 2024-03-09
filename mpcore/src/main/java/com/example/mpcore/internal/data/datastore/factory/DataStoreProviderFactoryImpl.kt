package com.example.mpcore.internal.data.datastore.factory

import com.example.mpcore.internal.data.datastore.DataStoreProviderImpl
import com.example.mpcore.internal.data.datastore.IDataStoreProvider

internal object DataStoreProviderFactoryImpl: IDataStoreProviderFactory {
    override fun create(): IDataStoreProvider {
        return DataStoreProviderImpl
    }
}