package com.example.mpdataprovider.internal

import com.example.mpdataprovider.datadtore.internal.AudioDataStoreProviderImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class AudioDataStoreProviderImplTest{

    private val audioDataStoreProviderImpl = AudioDataStoreProviderImpl(AudioDataStoreFakeImplementation())

    @OptIn(ExperimentalCoroutinesApi::class)
    val backgroundScope = TestScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_lastPlayingSongId_update_and_get() = runTest {
        audioDataStoreProviderImpl.lastPlayingSong().updateValue(4L)
        backgroundScope.launch {
            val list = mutableListOf<Long>()
            with(audioDataStoreProviderImpl.lastPlayingSong().getValue()){
                val count = count()
                assert(count == 1)
                toList(list)
                assert(list.isNotEmpty())
                assert(list.contains(4L))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_lastPlayingSong_get_without_update() = runTest {
        backgroundScope.launch {
            val list = mutableListOf<Long>()
            with(audioDataStoreProviderImpl.lastPlayingSong().getValue()){
                val count = count()
                assert(count == 0)
                toList(list)
                assert(list.isEmpty())
                val single = single()
                assert(single == -1L)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_lastPlayingSongLastDuration_update_get() = runTest {
        audioDataStoreProviderImpl.lastPlayingSongLastDuration().updateValue(30)
        backgroundScope.launch {
            val list = mutableListOf<Int>()
            with(audioDataStoreProviderImpl.lastPlayingSongLastDuration().getValue()){
                val count = count()
                assert(count == 0)
                toList(list)
                assert(list.isEmpty())
                val single = single()
                assert(single == -1)
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_test_lastPlayingSongLastDuration_get_without_update() = runTest {
        backgroundScope.launch {
            val list = mutableListOf<Int>()
            with(audioDataStoreProviderImpl.lastPlayingSongLastDuration().getValue()){
                val count = count()
                assert(count == 1)
                toList(list)
                assert(list.isNotEmpty())
                assert(list.contains(30))
            }
        }
    }
}