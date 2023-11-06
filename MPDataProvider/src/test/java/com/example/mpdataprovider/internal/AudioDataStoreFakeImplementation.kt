package com.example.mpdataprovider.internal

import com.example.mpdataprovider.DataStore.internal.IAudioDataStore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.ConcurrentHashMap

class AudioDataStoreFakeImplementation: IAudioDataStore {

    private val datastoreCache = ConcurrentHashMap<String,(String)->List<Any>>()
    private val consumer = ConcurrentHashMap<String,(List<Any>)->Unit>()
    private var onAllValuesChanges: (()->Unit)? = null

    override suspend fun putInt(key: String, value: Int) {
        datastoreCache[key]?.let {
            val ollValues = it.invoke(key)
            val list = mutableListOf<Any>()
            list.addAll(ollValues)
            list.add(value)
            datastoreCache[key] = {arg->
                if (arg == key){
                    list
                }else{
                    emptyList()
                }
            }
            consumer[key]?.invoke(list)
        }?:run {
            datastoreCache[key] = {
                if (it == key){
                    listOf(value)
                }else{
                    emptyList()
                }
            }
            consumer[key]?.invoke(listOf(value))
        }
        onAllValuesChanges?.invoke()
    }

    override suspend fun putLong(key: String, value: Long) {
        datastoreCache[key]?.let {
            val ollValues = it.invoke(key)
            val list = mutableListOf<Any>()
            list.addAll(ollValues)
            list.add(value)
            datastoreCache[key] = {arg->
                if (arg == key){
                    list
                }else{
                    emptyList()
                }
            }
            consumer[key]?.invoke(list)
        }?:run {
            datastoreCache[key] = {
                if (it == key){
                    listOf(value)
                }else{
                    emptyList()
                }
            }
            consumer[key]?.invoke(listOf(value))
        }
        onAllValuesChanges?.invoke()
    }

    override suspend fun putFloat(key: String, value: Float) {
        datastoreCache[key]?.let {
            val ollValues = it.invoke(key)
            val list = mutableListOf<Any>()
            list.addAll(ollValues)
            list.add(value)
            datastoreCache[key] = {arg->
                if (arg == key){
                    list
                }else{
                    emptyList()
                }
            }
            consumer[key]?.invoke(list)
        }?:run {
            datastoreCache[key] = {
                if (it == key){
                    listOf(value)
                }else{
                    emptyList()
                }
            }
            consumer[key]?.invoke(listOf(value))
        }
        onAllValuesChanges?.invoke()
    }

    override suspend fun putString(key: String, value: String) {
        datastoreCache[key]?.let {
            val ollValues = it.invoke(key)
            val list = mutableListOf<Any>()
            list.addAll(ollValues)
            list.add(value)
            datastoreCache[key] = {arg->
                if (arg == key){
                    list
                }else{
                    emptyList()
                }
            }
            consumer[key]?.invoke(list)
        }?:run {
            datastoreCache[key] = {
                if (it == key){
                    listOf(value)
                }else{
                    emptyList()
                }
            }
            consumer[key]?.invoke(listOf(value))
        }
        onAllValuesChanges?.invoke()
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        datastoreCache[key]?.let {
            val ollValues = it.invoke(key)
            val list = mutableListOf<Any>()
            list.addAll(ollValues)
            list.add(value)
            datastoreCache[key] = {arg->
                if (arg == key){
                    list
                }else{
                    emptyList()
                }
            }
            consumer[key]?.invoke(list)
        }?:run {
            datastoreCache[key] = {
                if (it == key){
                    listOf(value)
                }else{
                    emptyList()
                }
            }
            consumer[key]?.invoke(listOf(value))
        }
        onAllValuesChanges?.invoke()
    }

    override fun getInt(key: String, defaultValue: Int): Flow<Int> {
        return callbackFlow {
            if (consumer.contains(key)){
                consumer[key] = {
                    val lastValue = it.last()
                    if (lastValue is Int){
                        trySend(lastValue)
                    }else{
                        trySend(defaultValue)
                    }
                }
            }else{
                trySend(defaultValue)
            }
        }
    }

    override fun getLong(key: String, defaultValue: Long): Flow<Long> {
        return callbackFlow {
            if (consumer.contains(key)){
                consumer[key] = {
                    val lastValue = it.last()
                    if (lastValue is Long){
                        trySend(lastValue)
                    }else{
                        trySend(defaultValue)
                    }
                }
                awaitClose { consumer.remove(key) }
            }else{
                trySend(defaultValue)
                awaitClose()
            }
        }
    }

    override fun getFloat(key: String, defaultValue: Float): Flow<Float> {
        return callbackFlow {
            if (consumer.contains(key)){
                consumer[key] = {
                    val lastValue = it.last()
                    if (lastValue is Float){
                        trySend(lastValue)
                    }else{
                        trySend(defaultValue)
                    }
                }
                awaitClose { consumer.remove(key) }
            }else{
                trySend(defaultValue)
                awaitClose()
            }
        }
    }

    override fun getString(key: String, defaultValue: String): Flow<String> {
        return callbackFlow {
            if (consumer.contains(key)){
                consumer[key] = {
                    val lastValue = it.last()
                    if (lastValue is String){
                        trySend(lastValue)
                    }else{
                        trySend(defaultValue)
                    }
                }
                awaitClose { consumer.remove(key) }
            }else{
                trySend(defaultValue)
                awaitClose()
            }
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Flow<Boolean> {
        return callbackFlow {
            if (consumer.contains(key)){
                consumer[key] = {
                    val lastValue = it.last()
                    if (lastValue is Boolean){
                        trySend(lastValue)
                    }else{
                        trySend(defaultValue)
                    }
                }
                awaitClose { consumer.remove(key) }
            }else{
                trySend(defaultValue)
                awaitClose()
            }
        }
    }

    override fun getAll(): Flow<List<Any>> {
       return callbackFlow {
           val tempFun = {
               val list = mutableListOf<Any>()
               consumer.forEach {
                   consumer[it.key] = {values->
                       list.addAll(values)
                   }
               }
               trySend(list)
           }
           tempFun()
           onAllValuesChanges = {
               tempFun()
           }
           awaitClose { onAllValuesChanges = null }
       }
    }

    override suspend fun remove(key: String) {
        datastoreCache.remove(key)
        consumer.remove(key)
    }

    override suspend fun clear() {
        datastoreCache.clear()
        consumer.clear()
    }
}