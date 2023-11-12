package com.example.mpdataprovider.datastore

import kotlinx.coroutines.flow.Flow

interface IDataStoreController<T> {

    suspend fun updateValue(value: T)

    fun getValue(): Flow<T>
}