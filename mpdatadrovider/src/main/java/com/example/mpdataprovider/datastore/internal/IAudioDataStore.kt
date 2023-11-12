package com.example.mpdataprovider.datastore.internal

import kotlinx.coroutines.flow.Flow

internal interface IAudioDataStore {

    suspend fun putInt(key: String, value: Int)

    suspend fun putLong(key: String, value: Long)

    suspend fun putFloat(key: String, value: Float)

    suspend fun putString(key: String, value: String)

    suspend fun putBoolean(key: String, value: Boolean)

    fun getInt(key: String,defaultValue: Int): Flow<Int>

    fun getLong(key: String,defaultValue: Long): Flow<Long>

    fun getFloat(key: String, defaultValue: Float): Flow<Float>

    fun getString(key: String, defaultValue: String): Flow<String>

    fun getBoolean(key: String, defaultValue: Boolean): Flow<Boolean>

    fun getAll(): Flow<List<Any>>

    suspend fun remove(key: String)

    suspend fun clear()
}