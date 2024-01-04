package com.example.mpstorage.synchronizer.internal

import androidx.test.platform.app.InstrumentationRegistry
import com.example.mpdataprovider.datastore.AudioDataStoreApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test


class DataBaseSynchronizeTest{

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun test_synchronize_with_existedData_in_contentProvider_and_np_data_in_database()= runTest {
        val  dataSync = NormalDataSourceSynchronizationFake
        val dataProvider = FakeAudioDataProvider
        val synchronize = DataBaseSynchronize(dataProvider,dataSync)
        var isStarting = false
        var isCompleted = false
        var isFailed = false
        synchronize.setOnSynchronizeStartedListener {
            isStarting = true
            true
        }
        synchronize.setOnSynchronizeCompletedListener {
            isCompleted = true
            true
        }
        synchronize.setOnSynchronizeFailed {
            isFailed = true
            true
        }
        synchronize.synchronize(context)
        var count = 0
        val syncAudioList = dataSync.getAudioList(context)
        syncAudioList.forEach {
            if (dataProvider.getById(it.id) != null){
                count++
            }
        }
        assert(dataProvider.getAll().size == syncAudioList.size)
        assert(syncAudioList.size == count)
        assert(isStarting)
        assert(isCompleted)
        assert(!isFailed)
    }

    @Test
    fun test_synchronize_with_noData_in_contentProvider_and_np_data_in_database()= runTest {
        val  dataSync = NormalDataSourceSynchronizationFake
        val dataProvider = FakeAudioDataProvider
        val synchronize = DataBaseSynchronize(dataProvider,dataSync)
        var isStarting = false
        var isCompleted = false
        var isFailed = false
        synchronize.setOnSynchronizeStartedListener {
            isStarting = true
            true
        }
        synchronize.setOnSynchronizeCompletedListener {
            isCompleted = true
            true
        }
        synchronize.setOnSynchronizeFailed {
            isFailed = true
            true
        }
        synchronize.synchronize(context)
        var count = 0
        val syncAudioList = dataSync.getAudioList(context)
        val dataProviderList = dataProvider.getAll()
        syncAudioList.forEach {
            if (dataProvider.getById(it.id) != null){
                count++
            }
        }
        assert(dataProviderList.size == syncAudioList.size)
        assert(syncAudioList.size == count)
        assert(dataProviderList.isEmpty())
        assert(isStarting)
        assert(isCompleted)
        assert(!isFailed)
    }

    @Test
    fun test_synchronize_with_withData_in_contentProvider_and_some_data_in_database() = runTest {
        val  dataSync = NormalDataSourceSynchronizationFake
        val dataProvider = FakeAudioDataProvider
        val synchronize = DataBaseSynchronize(dataProvider,dataSync)
        var isStarting = false
        var isCompleted = false
        var isFailed = false
        synchronize.setOnSynchronizeStartedListener {
            isStarting = true
            true
        }
        synchronize.setOnSynchronizeCompletedListener {
            isCompleted = true
            true
        }
        synchronize.setOnSynchronizeFailed {
            isFailed = true
            true
        }
        backgroundScope.launch {
            synchronize.synchronize(context)
            var count = 0
            val syncAudioList = async { dataSync.getAudioList(context) }.await()
            syncAudioList.forEach {
                if (async { dataProvider.getById(it.id) }.await() != null){
                    count++
                }
            }
            assert(syncAudioList.size == syncAudioList.size)
            assert(syncAudioList.size == count)
            assert(isStarting)
            assert(isCompleted)
            assert(!isFailed)
        }
    }

    @Test
    fun test_synchronize_with_noData_in_contentProvider_and_same_data_in_database()= runTest {
        AudioDataStoreApi.isSynchronisationWithContentProviderCompleted(context).updateValue(true)
        val  dataSync = NormalDataSourceSynchronizationFake
        val dataProvider = FakeAudioDataProvider
        val synchronize = DataBaseSynchronize(dataProvider,dataSync)
        var isStarting = false
        var isCompleted = false
        var isFailed = false
        synchronize.setOnSynchronizeStartedListener {
            isStarting = true
            true
        }
        synchronize.setOnSynchronizeCompletedListener {
            isCompleted = true
            true
        }
        synchronize.setOnSynchronizeFailed {
            isFailed = true
            true
        }
        backgroundScope.launch {
            synchronize.synchronize(context)
            var count = 0
            val syncAudioList = dataSync.getAudioList(context)
            val dataProviderList = dataProvider.getAll()
            syncAudioList.forEach {
                if (dataProvider.getById(it.id) != null){
                    count++
                }
            }
            assert(dataProviderList.size == syncAudioList.size)
            assert(syncAudioList.size == count)
            assert(isStarting)
            assert(isCompleted)
            assert(!isFailed)
        }
        backgroundScope.launch {
            with(dataProvider.observeAll()){
                assert(count() == 1)
            }
        }
    }

    @Test
    fun test_synchronize_with_noData_in_contentProvider_and_same_data_in_database_with_noPermission()= runTest {
        val  dataSync = NormalDataSourceSynchronizationFake
        val dataProvider = FakeAudioDataProvider
        val synchronize = DataBaseSynchronize(dataProvider,dataSync)
        var isStarting = false
        var isCompleted = false
        var isFailed = false
        synchronize.setOnSynchronizeStartedListener {
            isStarting = true
            true
        }
        synchronize.setOnSynchronizeCompletedListener {
            isCompleted = true
            true
        }
        synchronize.setOnSynchronizeFailed {
            isFailed = true
            true
        }
        synchronize.synchronize(context)
        val dataProviderList = dataProvider.getAll()
        assert(dataProviderList.isEmpty())
        assert(isStarting)
        assert(!isCompleted)
        assert(isFailed)
    }

    @Test
    fun test_synchronize_with_noData_in_contentProvider_and_same_data_in_database_with_databaseError() = runTest {
        val  dataSync = NormalDataSourceSynchronizationFake
        val dataProvider = FakeAudioDataProvider
        val synchronize = DataBaseSynchronize(dataProvider,dataSync)
        var isStarting = false
        var isCompleted = false
        var isFailed = false
        synchronize.setOnSynchronizeStartedListener {
            isStarting = true
            true
        }
        synchronize.setOnSynchronizeCompletedListener {
            isCompleted = true
            true
        }
        synchronize.setOnSynchronizeFailed {
            isFailed = true
            true
        }
        synchronize.synchronize(context)
        val dataProviderList = dataProvider.getAll()
        assert(dataProviderList.isEmpty())
        assert(isStarting)
        assert(!isCompleted)
        assert(isFailed)

        backgroundScope.launch {
            val list = mutableListOf<Boolean>()
            with(AudioDataStoreApi.isSynchronisationWithContentProviderCompleted(context).getValue()){
                toList(list)
                assert(list.isNotEmpty())
                assert(!single())
                assert(!list.contains(true))
            }
        }
    }
}