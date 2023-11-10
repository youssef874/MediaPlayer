package com.example.mpdataprovider.internal

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mpdataprovider.datadtore.internal.AudioDataStoreImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AudioDataStoreImplTest{
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @OptIn(ExperimentalCoroutinesApi::class)
    val backgroundScope = TestScope()

    private val stringKey = "test_key"
    private val stringValue = "test"

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_putString() = runTest {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = this,
            produceFile = {
                appContext.preferencesDataStoreFile("test")
            }
        )
        val dataStoreImpl = AudioDataStoreImpl(testDataStore)
            val list = mutableListOf<String>()
            backgroundScope.launch{
                dataStoreImpl.clear()
                dataStoreImpl.putString(stringKey,stringValue)
                dataStoreImpl.getString(stringKey,"").toList(list)
                val contain = list.contains(stringValue)
                val containDefault = list.contains("")
                assert(contain)
                assert(!containDefault)
            }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getString_without_put() = runTest {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = this,
            produceFile = {
                appContext.preferencesDataStoreFile("test")
            }
        )
        val dataStoreImpl = AudioDataStoreImpl(testDataStore)
        val list = mutableListOf<String>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)){
            dataStoreImpl.clear()
            with(dataStoreImpl.getString("ttt","b")){
                val test = count()
                assert(test == 1)
                val first = single()
                assert(first == "b")
                toList(list)
                val contain = list.contains(stringValue)
                assert(!contain)
            }
            val single = dataStoreImpl.getString("ttt","").single()
            val defaultContain = single == ""
            assert(defaultContain)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_remove_fromDataStore() = runTest{
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = this,
            produceFile = {
                appContext.preferencesDataStoreFile("test")
            }
        )
        val dataStoreImpl = AudioDataStoreImpl(testDataStore)
        val list = mutableListOf<String>()
        backgroundScope.launch {
            dataStoreImpl.clear()
            dataStoreImpl.putString(stringKey,stringValue)
            dataStoreImpl.remove(stringKey)
            with(dataStoreImpl.getString(stringKey,"a")){
                toList(list)
                assert(list.isEmpty())
                val count = count()
                assert(count == 1)
                val first = single()
                assert(first == "a")
            }
        }
    }

    val boolKey = "is_test"
    val boolValue = true


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun tes_getAll_fromDataStore() = runTest {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = this,
            produceFile = {
                appContext.preferencesDataStoreFile("test")
            }
        )
        val dataStoreImpl = AudioDataStoreImpl(testDataStore)
        val list = mutableListOf<Any>()
        backgroundScope.launch {
            dataStoreImpl.putString(stringKey,stringValue)
            dataStoreImpl.putBoolean(boolKey,boolValue)
            with(dataStoreImpl.getAll()){
                val count = count()
                assert(count == 2)
                toList(list)
                assert(list.isNotEmpty())
                assert(list.contains(boolValue))
                assert(list.contains(stringValue))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_clear_dataStore() = runTest{
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = this,
            produceFile = {
                appContext.preferencesDataStoreFile("test")
            }
        )
        val dataStoreImpl = AudioDataStoreImpl(testDataStore)
        val list = mutableListOf<Any>()
        backgroundScope.launch {
            dataStoreImpl.putString(stringKey,stringValue)
            dataStoreImpl.putBoolean(boolKey,boolValue)
            dataStoreImpl.clear()
            with(dataStoreImpl.getAll()){
                val count = count()
                assert(count == 0)
                toList(list)
                assert(list.isEmpty())
            }
        }
    }

    val secondStringValue = "just Some test"

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun test_update_key_two_times() = runTest {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = this,
            produceFile = {
                appContext.preferencesDataStoreFile("test")
            }
        )
        val dataStoreImpl = AudioDataStoreImpl(testDataStore)
        val list = mutableListOf<String>()
        backgroundScope.launch {
            dataStoreImpl.putString(stringKey,stringValue)
            dataStoreImpl.putString(stringKey,secondStringValue)
            with(dataStoreImpl.getString(stringKey,"sc")){
                val count = count()
                assert(count == 2)
                toList(list)
                assert(list.isNotEmpty())
                assert(list.contains(stringValue))
                assert(list.contains(secondStringValue))
                assert(list.last() == secondStringValue)
            }
        }
    }
}