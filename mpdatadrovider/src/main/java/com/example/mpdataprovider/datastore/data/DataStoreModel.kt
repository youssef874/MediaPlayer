package com.example.mpdataprovider.datastore.data

internal data class DataStoreModel<T>(
    val key: String,
    val defaultValue: T
)
