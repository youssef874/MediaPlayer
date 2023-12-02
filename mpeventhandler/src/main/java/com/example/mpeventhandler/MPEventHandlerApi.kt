package com.example.mpeventhandler

import com.example.mpeventhandler.data.MPEvent
import com.example.mpeventhandler.internal.IEventCanceler
import com.example.mpeventhandler.internal.MPEventListener
import com.example.mpeventhandler.internal.factory.EventSubscriberFactoryImpl
import kotlinx.coroutines.flow.Flow

object MPEventHandlerApi {

    /**
     * Call this method to dispatch event
     * @param event: [MPEvent]
     */
    fun dispatchEvent(event: MPEvent){
        EventSubscriberFactoryImpl.create().onNext(event)
    }

    /**
     * Call this method to subscribe for one shot subscription
     * which mean once the event delivered the the subscription will be canceled
     * @param events: events subscribe to
     * @param listener: event callback
     */
    fun onShotSubscription(events: Array<MPEvent>, listener: MPEventListener){
        EventSubscriberFactoryImpl.create().oneShotSubscriber(events, listener)
    }

    /**
     * Call this method to subscribe to event freely
     * @param events: events subscribe to
     * @param listener: event callback
     * @return [IEventCanceler] to cancel the event subscription with
     */
    fun subscribe(events: Array<MPEvent>, listener: MPEventListener): IEventCanceler{
        return EventSubscriberFactoryImpl.create().subscribe(events, listener)
    }

    fun collectEvents(events: Array<String>): Flow<MPEvent>{
        return EventSubscriberFactoryImpl.create().collectEvent(events)
    }
}