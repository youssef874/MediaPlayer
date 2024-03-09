package com.example.mpcore.api.data.datastore

import kotlinx.coroutines.flow.Flow

interface IMPDataStoreManager<T> {

    suspend fun updateValue(data: T)

    fun observeValue(): Flow<T>
}