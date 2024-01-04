package com.example.mpstorage.synchronizer.factory

import com.example.mpstorage.synchronizer.internal.DataSourceForSynchronization
import com.example.mpstorage.synchronizer.internal.IDataSourceForSynchronization

internal object DataSourceSynchronizerFactoryImpl: IDataSourceSynchronizerFactory {
    override fun create(): IDataSourceForSynchronization {
        return DataSourceForSynchronization
    }
}