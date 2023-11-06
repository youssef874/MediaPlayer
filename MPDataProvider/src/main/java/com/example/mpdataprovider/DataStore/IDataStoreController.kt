package com.example.mpdataprovider.DataStore

import kotlinx.coroutines.flow.Flow

interface IDataStoreController<T> {

    suspend fun updateValue(value: T)

    fun getValue(): Flow<T>
}