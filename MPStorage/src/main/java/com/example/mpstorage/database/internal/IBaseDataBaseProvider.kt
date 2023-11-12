package com.example.mpstorage.database.internal

import com.example.mpstorage.database.data.BaseDatabaseData
import com.example.mpstorage.database.data.Search
import kotlinx.coroutines.flow.Flow

interface IBaseDataBaseProvider<T: BaseDatabaseData,Q: Search> {

    suspend fun add(data: T)

    suspend fun update(data: T)

    suspend fun delete(data: T)

    fun observeAll(): Flow<List<T>>

    suspend fun getAll(): List<T>

    fun getById(id: Long): Flow<T>

    fun  query(query: Q): Flow<List<T>>
}