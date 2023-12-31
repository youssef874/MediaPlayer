package com.example.mpdataprovider.internal

import com.example.mpdataprovider.datastore.RepeatMode
import com.example.mpdataprovider.datastore.internal.AudioDataStoreProviderImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AudioDataStoreProviderImplTest{

    private val audioDataStoreProviderImpl = AudioDataStoreProviderImpl(AudioDataStoreFakeImplementation())

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
                assert(count == 1)
                toList(list)
                assert(list.isEmpty())
                val single = single()
                assert(list.isNotEmpty())
                assert(single == 30)
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
                assert(count == 0)
                toList(list)
                assert(list.contains(-1))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_isInRandomMode_update_get()= runTest {
        audioDataStoreProviderImpl.isInRandomMode().updateValue(true)
        backgroundScope.launch {
            val list = mutableListOf<Boolean>()
            with(audioDataStoreProviderImpl.isInRandomMode().getValue()){
                val count = count()
                assert(count == 1)
                toList(list)
                assert(list.isNotEmpty())
                val single = single()
                assert(single)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_isInRandomMode__get_without_update()= runTest {
        backgroundScope.launch {
            val list = mutableListOf<Boolean>()
            with(audioDataStoreProviderImpl.isInRandomMode().getValue()){
                assert(count() == 0)
                toList(list)
                assert(!single())
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_repeatMode__update_get() = runTest {
        audioDataStoreProviderImpl.repeatMode().updateValue(RepeatMode.REPEAT_ALL)
        backgroundScope.launch {
            val list = mutableListOf<@RepeatMode Int>()
            with(audioDataStoreProviderImpl.repeatMode().getValue()){
                assert(count()==1)
                toList(list)
                assert(list.isNotEmpty())
                assert(single()==RepeatMode.REPEAT_ALL)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_repeatMode_get_without_update()= runTest {
        backgroundScope.launch {
            val list = mutableListOf<@RepeatMode Int>()
            with(audioDataStoreProviderImpl.repeatMode().getValue()){
                assert(count()==0)
                toList(list)
                assert(single()==RepeatMode.NO_REPEAT)
            }
        }
    }
}