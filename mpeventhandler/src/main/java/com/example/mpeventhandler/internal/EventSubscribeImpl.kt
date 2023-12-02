package com.example.mpeventhandler.internal

import com.example.mpeventhandler.data.MPEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

internal object EventSubscribeImpl: IEventSubscriber {

    private var subscribers = setOf<EventHandler>()

    override fun  onNext(event: MPEvent): Boolean {
        var isConsumed = false
        subscribers.forEach {
            if (!it.isDisposed){
                it.onNext(event)
                isConsumed = true
            }
        }
        subscribers = subscribers.filterNot (IEventCanceler::isDisposed).toSet()
        return isConsumed
    }

    override fun  subscribe(
        events: Array<MPEvent>,
        listener: MPEventListener
    ): IEventCanceler {
        val filter = {mpEvent: MPEvent->
            mpEvent.type in events.map { it.type }
        }
        val subs = EventHandler(filter, listener)
        subscribers = subscribers+ subs
        subs.onEventCanceled = {
            subscribers = subscribers - subs
        }
        return subs
    }

    override fun collectEvent(events: Array<String>)= callbackFlow {
        val filter = {mpEvent: MPEvent->
            mpEvent.type in events
        }
        val subs = EventHandler(filter,object : MPEventListener{
            override fun onEvent(event: MPEvent) {
                trySend(event)
            }
        })
        subscribers = subscribers+ subs
        awaitClose {
            subs.dispose()
            subs.let {
                subscribers = subscribers - it
            }
        }

    }

    override fun oneShotSubscriber(events: Array<MPEvent>, listener: MPEventListener) {
        val filter = { mpEvent: MPEvent->
            mpEvent.type in events.map { it.type }
        }
        val subs = EventHandler(filter, listener)
        subscribers = subscribers+ subs
        subs.onEventDelivered = {
            subs.dispose()
            subscribers = subscribers - subs
        }
    }
}