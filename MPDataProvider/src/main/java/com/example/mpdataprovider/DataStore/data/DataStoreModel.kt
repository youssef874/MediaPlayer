package com.example.mpdataprovider.DataStore.data

internal data class DataStoreModel<T>(
    val key: String,
    val defaultValue: T
)
