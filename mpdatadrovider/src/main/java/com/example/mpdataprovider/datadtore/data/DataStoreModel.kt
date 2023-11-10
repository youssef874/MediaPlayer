package com.example.mpdataprovider.datadtore.data

internal data class DataStoreModel<T>(
    val key: String,
    val defaultValue: T
)
