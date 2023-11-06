package com.example.mpdataprovider.DataStore.internal

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mplog.MPLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AudioDataStoreImpl(private val dataStore: DataStore<Preferences>): IAudioDataStore {

    override suspend fun putInt(key: String, value: Int) {
        MPLogger.d(CLASS_NAME,"putInt", TAG,"key: $key, value: $value")
        val intPreferenceKey = intPreferencesKey(key)
        dataStore.edit {
            it[intPreferenceKey] = value
        }
    }

    override suspend fun putLong(key: String, value: Long) {
        Log.d(CLASS_NAME,"$TAG [putLong] key: $key, value: $value")
        val longKey = longPreferencesKey(key)
        dataStore.edit {
            it[longKey] = value
        }
    }

    override suspend fun putFloat(key: String, value: Float) {
        MPLogger.d(CLASS_NAME,"putFloat", TAG,"key: $key, value: $value")
        val floatKey = floatPreferencesKey(key)
        dataStore.edit {
            it[floatKey] = value
        }
    }

    override suspend fun putString(key: String, value: String) {
        MPLogger.d(CLASS_NAME,"putString", TAG,"key: $key, value: $value")
        val stringPreference = stringPreferencesKey(key)
        dataStore.edit {
            it[stringPreference] = value
        }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        MPLogger.d(CLASS_NAME,"putBoolean", TAG,"key: $key, value: $value")
        val booleanKey = booleanPreferencesKey(key)
        dataStore.edit {
            it[booleanKey] = value
        }
    }

    override fun getInt(key: String, defaultValue: Int): Flow<Int> = dataStore.data.map {
        MPLogger.d(CLASS_NAME,"getInt", TAG,"key: $key, defaultValue: $defaultValue")
        try {
            val intPreferenceKey = intPreferencesKey(key)
            it[intPreferenceKey]?:defaultValue
        }catch (e: ClassCastException){
            defaultValue
        }
    }

    override fun getLong(key: String, defaultValue: Long): Flow<Long> = dataStore.data.map {
        MPLogger.d(CLASS_NAME,"getLong", TAG,"key: $key, defaultValue: $defaultValue")
        try {
            val longKey = longPreferencesKey(key)
            it[longKey] ?:defaultValue
        }catch (e: ClassCastException){
            Log.d(CLASS_NAME,"$TAG [getLong] key: $key, defaultValue: $defaultValue, message: ${e.message}")
            defaultValue
        }
    }

    override fun getFloat(key: String, defaultValue: Float): Flow<Float> = dataStore.data.map {
        MPLogger.d(CLASS_NAME,"getFloat", TAG,"key: $key, defaultValue: $defaultValue")
        try {
            val floatKey = floatPreferencesKey(key)
            it[floatKey] ?:defaultValue
        }catch (e: ClassCastException){
            Log.d(CLASS_NAME,"$TAG [getFloat] key: $key, defaultValue: $defaultValue, message: ${e.message}")
            defaultValue
        }
    }

    override fun getString(key: String, defaultValue: String): Flow<String> = dataStore.data.map {
        MPLogger.d(CLASS_NAME,"getString", TAG,"key: $key, defaultValue: $defaultValue")
        try {
            val stringPreference = stringPreferencesKey(key)
            it[stringPreference] ?:defaultValue
        }catch (e: ClassCastException){
            Log.e(CLASS_NAME,"$TAG [getString] key: $key, defaultValue: $defaultValue, message: ${e.message}")
            defaultValue
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Flow<Boolean> = dataStore.data.map {
        MPLogger.d(CLASS_NAME,"getBoolean", TAG,"key: $key, defaultValue: $defaultValue")
        try {
            val booleanKey = booleanPreferencesKey(key)
            it[booleanKey] ?:defaultValue
        }catch (e: ClassCastException){
            Log.e(CLASS_NAME,"$TAG [getBoolean] key: $key, defaultValue: $defaultValue, message: ${e.message}")
            defaultValue
        }
    }

    override fun getAll(): Flow<List<Any>> =dataStore.data.map {
        MPLogger.d(CLASS_NAME,"getAll", TAG,"all data")
        it.asMap().values.toList()
    }

    override suspend fun remove(key: String) {
        dataStore.edit {
            MPLogger.d(CLASS_NAME,"remove", TAG,"key: $key")
            remove(key)
        }
    }

    override suspend fun clear() {
        dataStore.edit {
            MPLogger.d(CLASS_NAME,"clear", TAG,"clear all data")
            clear()
        }
    }

    companion object{
        const val FILE_NAME = "myPreference"
        private const val CLASS_NAME = "AudioDataStoreImpl"
        private const val TAG = "DATA_STORE"
    }
}