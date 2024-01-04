package com.example.mpstorage.synchronizer.factory

import com.example.mpstorage.synchronizer.internal.IDataSourceForSynchronization

internal interface IDataSourceSynchronizerFactory {

    fun create(): IDataSourceForSynchronization
}