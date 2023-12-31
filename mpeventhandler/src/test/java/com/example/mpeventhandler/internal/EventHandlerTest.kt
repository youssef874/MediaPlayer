package com.example.mpeventhandler.internal

import com.example.mpeventhandler.data.MPEvent
import org.junit.Test


class EventHandlerTest{

    class TestEvent: MPEvent(type = "test")


    @Test
    fun test_onNext(){
        var _event: MPEvent? = null
        var isDelivered = false
        val eventHandler = EventHandler(
            filter = {
                it.type == "test"
            },
            listener = object : MPEventListener{
                override fun onEvent(event: MPEvent) {
                    _event = event
                }

            }
        )
        eventHandler.onEventDelivered = {
            isDelivered = true
        }
        eventHandler.onNext(TestEvent())
        assert(_event!=null)
        assert(_event?.type == "test")
        assert(isDelivered)
    }

    @Test
    fun test_dispose(){
        var _event: MPEvent? = null
        var isCanceled = false
        val eventHandler = EventHandler(
            filter = {
                it.type == "test"
            },
            listener = object : MPEventListener{
                override fun onEvent(event: MPEvent) {
                    _event = event
                }

            }
        )
        eventHandler.onEventCanceled = {
            isCanceled = true
        }
        eventHandler.dispose()
        assert(isCanceled)
        assert(eventHandler.listener == null)
        assert(eventHandler.isDisposed)
        assert(_event == null)
    }
}