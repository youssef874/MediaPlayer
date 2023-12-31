package com.example.mpeventhandler.internal

import com.example.mpeventhandler.data.MPEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test


class EventSubscribeImplTest{


    private var eventSubscriber = EventSubscribeImpl


    @Test
    fun test_subscribe(){
        var isChanged = false
        val subs = eventSubscriber.subscribe(
            events = arrayOf(EventHandlerTest.TestEvent()),
            object : MPEventListener{
                override fun onEvent(event: MPEvent) {
                    isChanged = true
                }

            }
        )
        eventSubscriber.onNext(EventHandlerTest.TestEvent())
        assert(isChanged)
        assert(!(subs as IEventHandler).isDisposed)
        subs.dispose()
        assert(subs.isDisposed)
    }

    @Test
    fun test_oneShotSubscriber(){
        var isChanged = false
        eventSubscriber.oneShotSubscriber(
            events = arrayOf(EventHandlerTest.TestEvent()),
            object : MPEventListener{
                override fun onEvent(event: MPEvent) {
                    isChanged = true
                }
            }
        )
        eventSubscriber.onNext(EventHandlerTest.TestEvent())
        assert(isChanged)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_collectEvent() = runTest {
        backgroundScope.launch {
            val list = mutableListOf<MPEvent>()
            with(eventSubscriber.collectEvent(arrayOf("test"))){
                toList(list)
                assert(count() != 0)
                assert(single() is EventHandlerTest.TestEvent)
                assert(list.isNotEmpty())
                assert(list.contains(EventHandlerTest.TestEvent()))
            }
        }
        eventSubscriber.onNext(EventHandlerTest.TestEvent())
    }
}